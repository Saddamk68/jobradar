package org.api.jobassist.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.api.jobassist.client.PythonClient;
import org.api.jobassist.client.dto.PythonAnalyzeResponse;
import org.api.jobassist.config.JobSearchProperties;
import org.api.jobassist.crawler.AtsCrawler;
import org.api.jobassist.crawler.CrawlerFactory;
import org.api.jobassist.entity.Company;
import org.api.jobassist.entity.CompanyAts;
import org.api.jobassist.entity.JobAnalysis;
import org.api.jobassist.entity.JobPosting;
import org.api.jobassist.metrics.JobSearchMetrics;
import org.api.jobassist.repository.JobAnalysisRepository;
import org.api.jobassist.repository.JobPostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyIngestionService {

    private final JobPostingRepository jobPostingRepository;
    private final JobAnalysisRepository jobAnalysisRepository;
    private final CrawlerFactory crawlerFactory;
    private final PythonClient pythonClient;
    private final JobSearchMetrics metrics;
    private final MeterRegistry meterRegistry;

    private final JobSearchProperties jobSearchProperties;

    @Transactional
    public void ingestCompany(CompanyAts mapping) {
        Company company = mapping.getCompany();
        log.info("Starting ingestion for company: {}", company.getName());

        Timer.Sample sample = Timer.start(meterRegistry);
        int newJobsCount = 0;
        int analyzedCount = 0;  // jobs sent to Python
        int savedCount = 0;     // jobs above threshold

        AtsCrawler crawler = crawlerFactory.getCrawler(
                mapping.getAtsPlatform().getName());

        List<JobPosting> crawledJobs = crawler.crawl(
                mapping.getAtsJobUrl(),
                company,
                mapping.getAtsPlatform()
        );
        int totalCrawled = crawledJobs.size();
        metrics.incrementCrawled(totalCrawled);

        Set<String> crawledUrls = new HashSet<>();
        Map<String, JobPosting> existingJobMap = new HashMap<>();

        if (!crawledJobs.isEmpty()) {
            List<String> crawledUrlList = crawledJobs.stream()
                    .map(JobPosting::getJobUrl)
                    .toList();

            List<JobPosting> existingJobs = jobPostingRepository.findByCompanyAndJobUrlIn(company, crawledUrlList);

            for (JobPosting existing : existingJobs) {
                existingJobMap.put(existing.getJobUrl(), existing);
            }
        }

        // Collect new jobs for batch Python analysis
        List<JobPosting> jobsToAnalyze = new ArrayList<>();

        for (JobPosting job : crawledJobs) {
            crawledUrls.add(job.getJobUrl());

            if (!isIndiaLocation(job)) continue;
            if (!isTechnicalTitle(job.getJobTitle())) continue;

            JobPosting existing = existingJobMap.get(job.getJobUrl());

            if (existing != null) {
                existing.setLastSeenAt(job.getLastSeenAt());
                existing.setActive(true);
            } else {
                if (!isRecentJob(job)) continue;

                job.setActive(true);
                job.setFirstSeenAt(LocalDateTime.now());
                job.setLastSeenAt(LocalDateTime.now());

                JobPosting savedJob = jobPostingRepository.save(job);
                jobsToAnalyze.add(savedJob);
                newJobsCount++;
            }
        }

        // Batch Python Analysis
        if (!jobsToAnalyze.isEmpty()) {
            List<PythonAnalyzeResponse> analyses = pythonClient.batchAnalyze(jobsToAnalyze);
            analyzedCount = analyses != null ? analyses.size() : 0;

            List<JobAnalysis> analysesToSave = new ArrayList<>();
            for (int i = 0; i < jobsToAnalyze.size(); i++) {
                if (i >= analyses.size()) break;

                JobPosting job = jobsToAnalyze.get(i);
                PythonAnalyzeResponse analysis = analyses.get(i);

                if (analysis == null || analysis.getMatchScore() == null) {
                    continue;
                }

                // Backfill postedDate from Python if the crawler didn't set it
                if (job.getPostedDate() == null && analysis.getPostedDate() != null) {
                    try {
                        job.setPostedDate(LocalDate.parse(analysis.getPostedDate()));
                    } catch (Exception e) {
                        log.warn("Failed to parse postedDate '{}' for job: {}",
                                analysis.getPostedDate(), job.getJobUrl());
                    }
                }

                // IMPORTANT: Filter low scores
                if (analysis.getMatchScore() < jobSearchProperties.getThreshold()) {
                    continue;
                }

                JobAnalysis jobAnalysis = JobAnalysis.builder()
                        .job(job)
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

                analysesToSave.add(jobAnalysis);
                savedCount++;
            }

            if (!analysesToSave.isEmpty()) {
                jobAnalysisRepository.saveAll(analysesToSave);
            }
        }
        metrics.incrementAnalyzed(analyzedCount);
        metrics.incrementSaved(savedCount);

        // Mark stale jobs inactive
        List<JobPosting> existingActive =
                jobPostingRepository.findByCompanyAndActiveTrue(company);

        for (JobPosting existingJob : existingActive) {
            if (!crawledUrls.contains(existingJob.getJobUrl())) {
                existingJob.setActive(false);
            }
        }

        sample.stop(metrics.getIngestionTimer());
        log.info("""
                        Company ingestion completed.
                        Company: {}
                        Total Crawled: {}
                        New Jobs: {}
                        Analysed: {}
                        Saved (above threshold): {}
                        """,
                company.getName(),
                totalCrawled,
                newJobsCount,
                analyzedCount,
                savedCount
        );
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
            return location.contains("remote")
                    || location.contains("india")
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
            return text.contains("remote")
                    || text.contains("india")
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

    // NEW — uses postedDate, with safe fallback
    private boolean isRecentJob(JobPosting job) {
        // Primary check: use postedDate if available
        if (job.getPostedDate() != null) {
            return job.getPostedDate().isAfter(
                    LocalDate.now().minusDays(14)
            );
        }

        // Fallback: use lastSeenAt if postedDate is missing
        if (job.getLastSeenAt() != null) {
            return job.getLastSeenAt().isAfter(
                    LocalDateTime.now().minusDays(14)
            );
        }

        // If neither field is set, allow the job through rather than silently dropping it
        return true;
    }

}
