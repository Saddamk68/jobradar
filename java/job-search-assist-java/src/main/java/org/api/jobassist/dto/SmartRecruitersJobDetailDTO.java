package org.api.jobassist.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmartRecruitersJobDetailDTO {

    private String postingUrl;
    private String description;

}
