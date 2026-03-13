package org.jobradar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobradar.entity.JobAnalysis;
import org.jobradar.entity.JobPosting;
import org.jobradar.repository.JobAnalysisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobAlertService {

    private static final double MIN_SCORE_THRESHOLD = 0.4;
    private static final int MAX_JOBS_PER_COMPANY = 10;

    private final JobAnalysisRepository jobAnalysisRepository;
    private final EmailService emailService;

    @Transactional
    public void sendDailyDigest() {

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        List<JobAnalysis> recentAnalyses =
                jobAnalysisRepository.findRecentUnnotifiedAboveScore(yesterday, MIN_SCORE_THRESHOLD);

        if (recentAnalyses.isEmpty()) {
            log.info("No recent qualifying jobs found. Skipping email.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // Group by company name
        Map<String, List<JobAnalysis>> byCompany = recentAnalyses.stream()
                .collect(Collectors.groupingBy(ja -> ja.getJob().getCompany().getName()));

        int totalEmailsSent = 0;
        StringBuilder summaryRows = new StringBuilder();

        for (Map.Entry<String, List<JobAnalysis>> entry : byCompany.entrySet()) {

            String companyName = entry.getKey();

            // Top N jobs for this company sorted by score descending
            List<JobAnalysis> topJobs = entry.getValue().stream()
                    .sorted(Comparator.comparingDouble(JobAnalysis::getMatchScore).reversed())
                    .limit(MAX_JOBS_PER_COMPANY)
                    .toList();

            if (topJobs.isEmpty()) continue;

            // Build per-company email
            String html = buildCompanyEmail(companyName, topJobs);

            String subject = String.format("🎯 JobRadar — %s (%d new match%s)",
                    companyName,
                    topJobs.size(),
                    topJobs.size() > 1 ? "es" : "");

            emailService.sendHtmlEmail("saddamk68@gmail.com", subject, html);

            // Mark all sent jobs as recommended
            topJobs.forEach(ja -> ja.getJob().setLastRecommendedAt(now));

            // Accumulate summary row
            double topScore = topJobs.get(0).getMatchScore();
            summaryRows.append("<tr>")
                    .append("<td style='padding:8px 12px;'>").append(companyName).append("</td>")
                    .append("<td style='padding:8px 12px;text-align:center;'>").append(topJobs.size()).append("</td>")
                    .append("<td style='padding:8px 12px;text-align:center;color:")
                    .append(topScore >= 0.8 ? "#27ae60" : "#e67e22").append(";'>")
                    .append(String.format("%.0f%%", topScore * 100)).append("</td>")
                    .append("</tr>");

            totalEmailsSent++;
            log.info("Sent company email — {} ({} jobs)", companyName, topJobs.size());
        }

        // Send summary email last
        if (totalEmailsSent > 0) {
            sendSummaryEmail(totalEmailsSent, byCompany.size(), summaryRows.toString());
        }

        log.info("Daily digest complete — {} company emails sent.", totalEmailsSent);
    }

    private String buildCompanyEmail(String companyName, List<JobAnalysis> jobs) {

        StringBuilder html = new StringBuilder();

        html.append("<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto;'>")
                .append("<div style='background:#2c3e50;padding:20px;border-radius:8px 8px 0 0;'>")
                .append("<h2 style='color:white;margin:0;'>🎯 JobRadar</h2>")
                .append("<p style='color:#bdc3c7;margin:4px 0 0;'>New matches at <b style='color:white;'>")
                .append(companyName).append("</b></p>")
                .append("</div>")
                .append("<div style='padding:16px;background:#f9f9f9;border-radius:0 0 8px 8px;'>");

        for (int i = 0; i < jobs.size(); i++) {

            JobAnalysis ja = jobs.get(i);
            JobPosting job = ja.getJob();

            String scoreColor = ja.getMatchScore() >= 0.8 ? "#27ae60" : "#e67e22";
            String postedDateStr = job.getPostedDate() != null
                    ? job.getPostedDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    : "Unknown";
            String locationStr = job.getLocation() != null && !job.getLocation().isBlank()
                    ? job.getLocation()
                    : "Not specified";
            String experienceStr = ja.getExperienceRange() != null
                    ? ja.getExperienceRange() + " yrs"
                    : "Not specified";
            String skillsStr = ja.getExtractedSkills() != null && !ja.getExtractedSkills().isBlank()
                    ? ja.getExtractedSkills()
                    : "—";

            html.append("<div style='background:white;border:1px solid #e0e0e0;border-radius:6px;")
                    .append("padding:14px;margin-bottom:12px;'>")
                    // Rank badge + title
                    .append("<table style='margin-bottom:8px;'><tr>")
                    .append("<td style='vertical-align:middle;padding-right:10px;'>")
                    .append("<span style='background:#3498db;color:white;border-radius:50%;")
                    .append("width:24px;height:24px;display:inline-block;text-align:center;")
                    .append("line-height:24px;font-size:12px;'>")
                    .append(i + 1).append("</span>")
                    .append("</td>")
                    .append("<td style='vertical-align:middle;'>")
                    .append("<b style='font-size:14px;'>").append(job.getJobTitle()).append("</b>")
                    .append("</td></tr></table>")
                    // Details grid
                    .append("<table style='width:100%;font-size:13px;color:#555;'>")
                    .append("<tr>")
                    .append("<td>📍 ").append(locationStr).append("</td>")
                    .append("<td>📅 ").append(postedDateStr).append("</td>")
                    .append("</tr><tr>")
                    .append("<td>🧑‍💻 Exp: ").append(experienceStr).append("</td>")
                    .append("<td>🛠 Skills: ").append(skillsStr).append("</td>")
                    .append("</tr>")
                    .append("</table>")
                    // Score + Apply
                    .append("<table style='width:100%;margin-top:10px;'><tr>")
                    .append("<td style='vertical-align:middle;'>")
                    .append("<span style='color:").append(scoreColor)
                    .append(";font-weight:bold;font-size:14px;'>⭐ ")
                    .append(String.format("%.0f%%", ja.getMatchScore() * 100))
                    .append(" match</span>")
                    .append("</td>")
                    .append("<td style='text-align:right;vertical-align:middle;'>")
                    .append("<a href='").append(job.getJobUrl()).append("' ")
                    .append("style='background:#3498db;color:white;padding:7px 16px;")
                    .append("text-decoration:none;border-radius:4px;font-size:13px;")
                    .append("display:inline-block;font-weight:bold;'>")
                    .append("Apply Now →</a>")
                    .append("</td>")
                    .append("</tr></table>")
                    .append("</div>");
        }

        html.append("<p style='color:#bdc3c7;font-size:11px;text-align:center;margin-top:8px;'>")
                .append("Powered by JobRadar</p>")
                .append("</div></div>");

        return html.toString();
    }

    private void sendSummaryEmail(int companiesCount, int totalCompaniesWithJobs, String tableRows) {

        String html = "<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto;'>"
                + "<div style='background:#2c3e50;padding:20px;border-radius:8px 8px 0 0;'>"
                + "<h2 style='color:white;margin:0;'>📊 JobRadar — Daily Summary</h2>"
                + "<p style='color:#bdc3c7;margin:4px 0 0;'>"
                + companiesCount + " companies had new matches today</p>"
                + "</div>"
                + "<div style='padding:16px;background:#f9f9f9;border-radius:0 0 8px 8px;'>"
                + "<table style='width:100%;border-collapse:collapse;background:white;"
                + "border-radius:6px;overflow:hidden;border:1px solid #e0e0e0;'>"
                + "<thead>"
                + "<tr style='background:#3498db;color:white;'>"
                + "<th style='padding:10px 12px;text-align:left;'>Company</th>"
                + "<th style='padding:10px 12px;'>Jobs</th>"
                + "<th style='padding:10px 12px;'>Top Score</th>"
                + "</tr>"
                + "</thead>"
                + "<tbody>" + tableRows + "</tbody>"
                + "</table>"
                + "<p style='color:#bdc3c7;font-size:11px;text-align:center;margin-top:12px;'>"
                + "Check individual company emails for full details</p>"
                + "</div></div>";

        emailService.sendHtmlEmail(
                "saddamk68@gmail.com",
                "📊 JobRadar Daily Summary — " + companiesCount + " companies",
                html);
    }

}
