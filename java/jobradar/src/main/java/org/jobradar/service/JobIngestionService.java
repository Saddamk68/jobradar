package org.jobradar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobradar.client.PythonClient;
import org.jobradar.client.dto.PythonAnalyzeResponse;
import org.jobradar.crawler.CrawlerFactory;
import org.jobradar.entity.Company;
import org.jobradar.entity.CompanyAts;
import org.jobradar.entity.JobAnalysis;
import org.jobradar.entity.JobPosting;
import org.jobradar.repository.CompanyAtsRepository;
import org.jobradar.repository.JobAnalysisRepository;
import org.jobradar.repository.JobPostingRepository;
import org.jobradar.crawler.AtsCrawler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobIngestionService {

    private final CompanyAtsRepository companyAtsRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CrawlerFactory crawlerFactory;
    private final PythonClient pythonClient;
    private final JobAnalysisRepository jobAnalysisRepository;

    @Transactional
    public void ingestJobs() {

        List<CompanyAts> mappings = companyAtsRepository.findByActiveTrue();

        for (CompanyAts mapping : mappings) {

            Company company = mapping.getCompany();

            AtsCrawler crawler = crawlerFactory.getCrawler(
                    mapping.getAtsPlatform().getName());

            List<JobPosting> crawledJobs = crawler.crawl(
                    mapping.getAtsJobUrl(),
                    company,
                    mapping.getAtsPlatform()
            );

            Set<String> crawledUrls = new HashSet<>();

            for (JobPosting job : crawledJobs) {

                crawledUrls.add(job.getJobUrl());

                JobPosting savedJob;

                Optional<JobPosting> existingOpt =
                        jobPostingRepository.findByJobUrl(job.getJobUrl());

                if (existingOpt.isPresent()) {

                    savedJob = existingOpt.get();
                    savedJob.setLastSeenAt(LocalDateTime.now());
                    savedJob.setActive(true);

                } else {

                    job.setActive(true);
                    savedJob = jobPostingRepository.save(job);

                    // 🔥 Only analyze NEW job
                    PythonAnalyzeResponse analysis =
                            pythonClient.analyze(savedJob.getJobDescription(), 5);

                    if (analysis != null) {

                        JobAnalysis jobAnalysis = JobAnalysis.builder()
                                .job(savedJob)
                                .matchScore(analysis.getMatchScore())
                                .extractedSkills(
                                        analysis.getExtractedSkills() != null
                                                ? String.join(",", analysis.getExtractedSkills())
                                                : null
                                )
                                .experienceRange(
                                        analysis.getExperienceDetected() != null
                                                ? analysis.getExperienceDetected().toString()
                                                : null
                                )
                                .signals(analysis.getRoleType())
                                .analyzedAt(LocalDateTime.now())
                                .build();

                        jobAnalysisRepository.save(jobAnalysis);
                    }
                }
            }

            // Mark stale jobs inactive
            List<JobPosting> existingActive =
                    jobPostingRepository.findByCompanyAndActiveTrue(company);

            for (JobPosting existingJob : existingActive) {

                if (!crawledUrls.contains(existingJob.getJobUrl())) {
                    existingJob.setActive(false);
                }
            }
        }

        log.info("Job ingestion cycle completed.");
    }

}
