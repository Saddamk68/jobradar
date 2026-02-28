package org.jobradar.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PythonAnalyzeRequest {

    private String jobDescription;
    private List<String> targetSkills;
    private int experienceYears;

}
