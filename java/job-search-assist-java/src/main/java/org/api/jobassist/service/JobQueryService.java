package org.api.jobassist.service;

import lombok.RequiredArgsConstructor;
import org.api.jobassist.dto.JobAnalysisResponseDTO;
import org.api.jobassist.entity.JobAnalysis;
import org.api.jobassist.repository.JobAnalysisRepository;
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
                        .postedDate(ja.getJob().getPostedDate())
                        .build()
                )
                .toList();
    }

}
