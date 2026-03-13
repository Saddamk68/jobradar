package org.api.jobassist.client;

import lombok.extern.slf4j.Slf4j;
import org.api.jobassist.client.dto.PythonAnalyzeRequest;
import org.api.jobassist.client.dto.PythonAnalyzeResponse;
import org.api.jobassist.entity.JobPosting;
import org.api.jobassist.entity.TargetSkill;
import org.api.jobassist.repository.TargetSkillRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class PythonClient {

    private final TargetSkillRepository targetSkillRepository;
    private final RestTemplate restTemplate;

    private static final String PYTHON_BASE_URL = "http://127.0.0.1:8000";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    // Circuit breaker config
    private static final int FAILURE_THRESHOLD = 5;
    private static final long CIRCUIT_OPEN_DURATION_MS = 60_000;

    // Circuit state
    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private volatile long circuitOpenedAt = 0;

    public PythonClient(TargetSkillRepository targetSkillRepository,
                        @Qualifier("pythonRestTemplate") RestTemplate restTemplate) {
        this.targetSkillRepository = targetSkillRepository;
        this.restTemplate = restTemplate;
    }

    public PythonAnalyzeResponse analyze(String jobDescription, int experienceYears) {
        if (isCircuitOpen()) {
            log.warn("Python circuit breaker is OPEN. Skipping analyze call.");
            return null;
        }

        List<String> skills = targetSkillRepository.findByActiveTrue()
                .stream()
                .map(TargetSkill::getSkillName)
                .toList();

        PythonAnalyzeRequest request = PythonAnalyzeRequest.builder()
                .jobDescription(jobDescription)
                .targetSkills(skills)
                .experienceYears(experienceYears)
                .build();

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                ResponseEntity<PythonAnalyzeResponse> response =
                        restTemplate.postForEntity(
                                PYTHON_BASE_URL + "/analyze",
                                request,
                                PythonAnalyzeResponse.class
                        );
                recordSuccess();
                return response.getBody();

            } catch (Exception e) {
                log.warn("Python analyze attempt {} failed", attempt, e);
                recordFailure();

                if (attempt == MAX_RETRIES) {
                    log.error("Python analyze failed after {} attempts", MAX_RETRIES);
                    return null;
                }
                sleep();
            }
        }
        return null;
    }

    public List<PythonAnalyzeResponse> batchAnalyze(List<JobPosting> jobs) {
        if (isCircuitOpen()) {
            log.warn("Python circuit breaker is OPEN. Skipping batch analyze.");
            return Collections.emptyList();
        }

        List<Map<String, Object>> requestBody = new ArrayList<>();
        List<String> skills = targetSkillRepository.findByActiveTrue()
                .stream()
                .map(TargetSkill::getSkillName)
                .toList();

        for (JobPosting job : jobs) {
            Map<String, Object> map = new HashMap<>();
            map.put("jobDescription", job.getJobDescription());
            map.put("targetSkills", skills);
            map.put("experienceYears", 5);
            requestBody.add(map);
        }

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                ResponseEntity<PythonAnalyzeResponse[]> response =
                        restTemplate.postForEntity(
                                PYTHON_BASE_URL + "/analyze/batch",
                                requestBody,
                                PythonAnalyzeResponse[].class
                        );
                recordSuccess();
                PythonAnalyzeResponse[] body = response.getBody();
                if (body == null) {
                    return Collections.emptyList();
                }
                return Arrays.asList(body);

            } catch (Exception e) {
                log.warn("Python batch analyze attempt {} failed", attempt, e);
                recordFailure();

                if (attempt == MAX_RETRIES) {
                    log.error("Python batch analyze failed after {} attempts", MAX_RETRIES);
                    return Collections.emptyList();
                }
                sleep();
            }
        }
        return Collections.emptyList();
    }

    private void sleep() {
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean isCircuitOpen() {
        if (consecutiveFailures.get() < FAILURE_THRESHOLD) {
            return false;
        }

        long now = System.currentTimeMillis();

        if (now - circuitOpenedAt > CIRCUIT_OPEN_DURATION_MS) {
            // Reset circuit after cooldown
            log.info("Circuit breaker resetting. Allowing Python calls again.");
            consecutiveFailures.set(0);
            return false;
        }

        return true;
    }

    private void recordFailure() {
        int failures = consecutiveFailures.incrementAndGet();

        if (failures == FAILURE_THRESHOLD) {
            circuitOpenedAt = System.currentTimeMillis();
            log.error("Circuit breaker OPENED after {} consecutive failures", FAILURE_THRESHOLD);
        }
    }

    private void recordSuccess() {
        consecutiveFailures.set(0);
    }

}
