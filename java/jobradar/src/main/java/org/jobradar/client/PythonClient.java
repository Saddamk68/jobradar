package org.jobradar.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobradar.client.dto.PythonAnalyzeRequest;
import org.jobradar.client.dto.PythonAnalyzeResponse;
import org.jobradar.entity.JobPosting;
import org.jobradar.entity.TargetSkill;
import org.jobradar.repository.TargetSkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PythonClient {

    private final TargetSkillRepository targetSkillRepository;
    private final RestTemplate restTemplate;

    private static final String PYTHON_BASE_URL = "http://127.0.0.1:8000";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    public PythonAnalyzeResponse analyze(String jobDescription, int experienceYears) {
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
                return response.getBody();

            } catch (Exception e) {
                log.warn("Python analyze attempt {} failed", attempt, e);

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

                return Arrays.asList(response.getBody());

            } catch (Exception e) {
                log.warn("Python batch analyze attempt {} failed", attempt, e);

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

}
