package org.jobradar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jobradar.client.PythonClient;
import org.jobradar.client.dto.PythonAnalyzeResponse;
import org.jobradar.crawler.AtsCrawler;
import org.jobradar.crawler.CrawlerFactory;
import org.jobradar.entity.Company;
import org.jobradar.entity.CompanyAts;
import org.jobradar.entity.JobAnalysis;
import org.jobradar.entity.JobPosting;
import org.jobradar.repository.CompanyAtsRepository;
import org.jobradar.repository.JobAnalysisRepository;
import org.jobradar.repository.JobPostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobIngestionService {

    private final CompanyAtsRepository companyAtsRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobAnalysisRepository jobAnalysisRepository;
    private final CrawlerFactory crawlerFactory;
    private final PythonClient pythonClient;

    public void ingestJobs() {

        List<CompanyAts> mappings = companyAtsRepository.findActiveWithAssociations();

        for (CompanyAts mapping : mappings) {

            try {
                ingestCompany(mapping);
            } catch (Exception e) {
                log.error("Failed ingestion for company: {}",
                        mapping.getCompany().getName(), e);
            }
        }

        log.info("Job ingestion cycle completed.");
    }

    @Transactional
    public void ingestCompany(CompanyAts mapping) {

        Company company = mapping.getCompany();

        log.info("Starting ingestion for company: {}", company.getName());

        AtsCrawler crawler = crawlerFactory.getCrawler(
                mapping.getAtsPlatform().getName());

        List<JobPosting> crawledJobs = crawler.crawl(
                mapping.getAtsJobUrl(),
                company,
                mapping.getAtsPlatform()
        );

        Set<String> crawledUrls = new HashSet<>();

        List<JobPosting> existingJobs =
                jobPostingRepository.findAllByCompany(company);

        Map<String, JobPosting> existingJobMap = new HashMap<>();

        for (JobPosting existing : existingJobs) {
            existingJobMap.put(existing.getJobUrl(), existing);
        }

        for (JobPosting job : crawledJobs) {

            crawledUrls.add(job.getJobUrl());

            // 🔥 India location filter
            if (!isIndiaLocation(job)) continue;

            // 🔥 Engineer / Developer title filter (flexible)
            if (!isTechnicalTitle(job.getJobTitle())) continue;

            JobPosting existing = existingJobMap.get(job.getJobUrl());

            if (existing != null) {
                existing.setLastSeenAt(job.getLastSeenAt());
                existing.setActive(true);
            } else {
                // 🔥 14-day filter using updatedAt (if available)
                if (!isRecentJob(job)) continue;

                job.setActive(true);
                JobPosting savedJob = jobPostingRepository.save(job);

                PythonAnalyzeResponse analysis =
                        pythonClient.analyze(savedJob.getJobDescription(), 5);

                if (analysis != null && analysis.getMatchScore() != null) {

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

        // 🔥 MARK STALE JOBS INACTIVE
        List<JobPosting> existingActive =
                jobPostingRepository.findByCompanyAndActiveTrue(company);

        for (JobPosting existingJob : existingActive) {
            if (!crawledUrls.contains(existingJob.getJobUrl())) {
                existingJob.setActive(false);
            }
        }

        log.info("Completed ingestion for company: {}", company.getName());
    }

    private boolean isTechnicalTitle(String title) {
        if (StringUtils.isBlank(title)) return false;

        String t = title.toLowerCase();
        return t.contains("engineer")
                || t.contains("developer")
                || t.contains("backend")
                || t.contains("java")
                || t.contains("software");
    }

    private boolean isIndiaLocation(JobPosting job) {
        if (!StringUtils.isBlank(job.getLocation())) {
            String location = job.getLocation().toLowerCase();
            return location.contains("india")
                    || location.contains("remote")
                    || location.contains("bangalore")
                    || location.contains("bengaluru")
                    || location.contains("hyderabad")
                    || location.contains("pune")
                    || location.contains("chennai")
                    || location.contains("jaipur")
                    || location.contains("gurgaon")
                    || location.contains("noida")
                    || location.contains("delhi ncr");
        } else {
            if (job.getJobDescription() == null) return false;

            String text = job.getJobDescription().toLowerCase();
            return text.contains("india")
                    || text.contains("remote")
                    || text.contains("bangalore")
                    || text.contains("bengaluru")
                    || text.contains("hyderabad")
                    || text.contains("pune")
                    || text.contains("chennai")
                    || text.contains("jaipur")
                    || text.contains("gurgaon")
                    || text.contains("noida")
                    || text.contains("delhi ncr");
        }
    }

    private boolean isRecentJob(JobPosting job) {
        if (job.getLastSeenAt() == null) {
            return false;
        }

        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(14);
        return job.getLastSeenAt().isAfter(fourteenDaysAgo);
    }

}
