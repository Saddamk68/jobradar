package org.api.jobassist.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class PythonAnalyzeResponse {

    private String postedDate;

    private List<String> extractedSkills;

    private List<String> missingSkills;

    private Double matchScore;

    private Integer experienceDetected;

    private String roleType;

}
