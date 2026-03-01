package org.jobradar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobAnalysisResponseDTO {

    private String companyName;
    private String jobTitle;
    private String jobUrl;
    private Double matchScore;
    private String extractedSkills;
    private String experienceRange;

}
