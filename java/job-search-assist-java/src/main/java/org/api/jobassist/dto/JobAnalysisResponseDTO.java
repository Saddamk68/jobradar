package org.api.jobassist.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class JobAnalysisResponseDTO {

    private String companyName;
    private String jobTitle;
    private String jobUrl;
    private Double matchScore;
    private String extractedSkills;
    private String experienceRange;
    private LocalDate postedDate;

}
