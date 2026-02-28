package org.jobradar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobradar.entity.JobAnalysis;
import org.jobradar.entity.JobPosting;
import org.jobradar.repository.JobAnalysisRepository;
import org.jobradar.repository.JobPostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobAlertService {

    private static final double MIN_SCORE_THRESHOLD = 0.4;

    private final JobPostingRepository jobPostingRepository;
    private final JobAnalysisRepository jobAnalysisRepository;
    private final EmailService emailService;

    @Transactional
    public void sendDailyDigest() {

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        List<JobPosting> recentJobs =
                jobPostingRepository
                        .findByActiveTrueAndFirstSeenAtAfterAndLastRecommendedAtIsNull(yesterday);

        if (recentJobs.isEmpty()) {
            log.info("No recent jobs found. Skipping email.");
            return;
        }

        Map<JobPosting, Double> scoredJobs = new HashMap<>();

        for (JobPosting job : recentJobs) {

            Optional<JobAnalysis> analysis =
                    jobAnalysisRepository.findByJob(job);

            if (analysis.isEmpty()) continue;

            double score = analysis.get().getMatchScore();

            // 🔥 Only include jobs above threshold
            if (score >= MIN_SCORE_THRESHOLD) {
                scoredJobs.put(job, score);
                log.info("Qualified job: {} | Score: {}", job.getJobTitle(), score);
            } else {
                log.info("Filtered out (low score): {} | Score: {}", job.getJobTitle(), score);
            }
        }

        if (scoredJobs.isEmpty()) {
            log.info("No high-signal jobs found. Skipping email.");
            return;
        }

        List<Map.Entry<JobPosting, Double>> topJobs =
                scoredJobs.entrySet()
                        .stream()
                        .sorted(Map.Entry.<JobPosting, Double>comparingByValue().reversed())
                        .limit(5)
                        .toList();

        StringBuilder html = new StringBuilder();
        html.append("<h2>Daily Job Radar Digest</h2>");

        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<JobPosting, Double> entry : topJobs) {

            JobPosting job = entry.getKey();
            double score = entry.getValue();

            html.append("<p>")
                    .append("<b>").append(job.getCompany().getName()).append("</b><br>")
                    .append(job.getJobTitle()).append("<br>")
                    .append("Score: ").append(String.format("%.2f", score)).append("<br>")
                    .append("<a href='").append(job.getJobUrl()).append("'>Apply</a>")
                    .append("</p><hr>");

            job.setLastRecommendedAt(now);
        }

        emailService.sendHtmlEmail(
                "saddamk68@gmail.com",
                "Daily Job Radar Digest",
                html.toString()
        );

        log.info("Daily digest email sent successfully.");
    }

}
