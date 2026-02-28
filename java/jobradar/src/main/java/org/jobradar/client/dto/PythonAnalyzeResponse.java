package org.jobradar.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class PythonAnalyzeResponse {

    private List<String> extractedSkills;

    private List<String> missingSkills;

    private Double matchScore;

    private Integer experienceDetected;

    private String roleType;

}
