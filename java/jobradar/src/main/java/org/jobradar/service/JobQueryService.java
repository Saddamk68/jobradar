package org.jobradar.service;

import lombok.RequiredArgsConstructor;
import org.jobradar.dto.JobAnalysisResponseDTO;
import org.jobradar.entity.JobAnalysis;
import org.jobradar.repository.JobAnalysisRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobQueryService {

    private final JobAnalysisRepository jobAnalysisRepository;

    public List<JobAnalysisResponseDTO> getTopJobs(double minScore) {
        List<JobAnalysis> analyses = jobAnalysisRepository.findTopByMinScore(minScore);

        return analyses.stream().map(ja -> JobAnalysisResponseDTO.builder()
                        .companyName(ja.getJob().getCompany().getName())
                        .jobTitle(ja.getJob().getJobTitle())
                        .jobUrl(ja.getJob().getJobUrl())
                        .matchScore(ja.getMatchScore())
                        .extractedSkills(ja.getExtractedSkills())
                        .experienceRange(ja.getExperienceRange())
                        .build()
                )
                .toList();
    }

}
