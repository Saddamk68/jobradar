package org.jobradar.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobradar.client.dto.PythonAnalyzeRequest;
import org.jobradar.client.dto.PythonAnalyzeResponse;
import org.jobradar.entity.TargetSkill;
import org.jobradar.repository.TargetSkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PythonClient {

    private final TargetSkillRepository targetSkillRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String PYTHON_URL = "http://127.0.0.1:8000/analyze";

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

        try {

            ResponseEntity<PythonAnalyzeResponse> response =
                    restTemplate.postForEntity(
                            PYTHON_URL,
                            request,
                            PythonAnalyzeResponse.class
                    );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error calling Python service", e);
            return null;
        }
    }

}
