package org.api.jobassist.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.api.jobassist.dto.SmartRecruitersJobDetailDTO;
import org.api.jobassist.entity.AtsPlatform;
import org.api.jobassist.entity.Company;
import org.api.jobassist.entity.JobPosting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SmartRecruitersCrawler implements AtsCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartRecruitersCrawler.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<JobPosting> crawl(String atsJobUrl, Company company, AtsPlatform platform) {
        List<JobPosting> jobs = new ArrayList<>();

        String apiUrl = null;
        try {
            String companyIdentifier = atsJobUrl.substring(atsJobUrl.lastIndexOf("/") + 1);

            apiUrl = "https://api.smartrecruiters.com/v1/companies/"
                    + companyIdentifier
                    + "/postings";

            String response = restTemplate.getForObject(apiUrl, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode postings = root.get("content");

            if (postings == null || !postings.isArray()) {
                return jobs;
            }

            for (JsonNode jobNode : postings) {

                // Language filter — skip non-English
                JsonNode langNode = jobNode.path("language");
                String languageCode = langNode.has("code") ? langNode.get("code").asText("") : "";
                if (!languageCode.isBlank() && !languageCode.equalsIgnoreCase("en")) {
                    LOGGER.debug("Skipping non-English job: {} [{}]", jobNode.get("name").asText(), languageCode);
                    continue;
                }

                String jobId = jobNode.get("id").asText();
                String title = jobNode.get("name").asText();
                String location = jobNode.path("location").path("city").asText("");

                // releasedDate is the actual posting date
                String releasedDateRaw = jobNode.has("releasedDate")
                        ? jobNode.get("releasedDate").asText()
                        : null;

                LocalDate postedDate = null;
                if (releasedDateRaw != null && !releasedDateRaw.isBlank()) {
                    try {
                        postedDate = OffsetDateTime.parse(releasedDateRaw).toLocalDate();
                    } catch (Exception e) {
                        LOGGER.warn("Failed to parse releasedDate '{}' for job: {}", releasedDateRaw, jobId);
                    }
                }

                // Detail call — gets postingUrl (real apply URL) + full description
                SmartRecruitersJobDetailDTO detail = fetchJobDetail(companyIdentifier, jobId);

                JobPosting job = JobPosting.builder()
                        .active(true)
                        .jobTitle(title)
                        .jobUrl(detail.getPostingUrl())   // postingUrl from detail, NOT ref from listing
                        .location(location)
                        .company(company)
                        .atsPlatform(platform)
                        .jobDescription(detail.getDescription())
                        .postedDate(postedDate)
                        .firstSeenAt(LocalDateTime.now())
                        .lastSeenAt(LocalDateTime.now())
                        .build();

                jobs.add(job);

                // Small delay to avoid rate limiting
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        } catch (HttpClientErrorException.NotFound e) {
            LOGGER.warn("SmartRecruiters company not found (404): {} -> {}", apiUrl, e.getMessage());
        } catch (HttpClientErrorException.BadRequest e) {
            LOGGER.warn("Bad request to SmartRecruiters API (400): {} -> {}", apiUrl, e.getMessage());
        } catch (HttpClientErrorException e) {
            LOGGER.warn("Client error calling SmartRecruiters API {}: {} -> {}", apiUrl, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.warn("Server error calling SmartRecruiters API {}: {} -> {}", apiUrl, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            LOGGER.warn("I/O error calling SmartRecruiters API {}: {}", apiUrl, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error crawling SmartRecruiters {}: {}", apiUrl, e.getMessage(), e);
        }

        return jobs;
    }

    /**
     * Fetches the job detail page which contains:
     * - postingUrl: the real human-readable apply URL (e.g. https://jobs.smartrecruiters.com/Visa/744000114540402-...)
     * - jobAd.sections: full structured job description
     * <p>
     * The listing API's "ref" field is an API URL that returns JSON — never use it as jobUrl.
     */
    private SmartRecruitersJobDetailDTO fetchJobDetail(String companyIdentifier, String jobId) {
        try {
            String detailUrl = "https://api.smartrecruiters.com/v1/companies/"
                    + companyIdentifier
                    + "/postings/"
                    + jobId;

            String response = restTemplate.getForObject(detailUrl, String.class);
            JsonNode root = objectMapper.readTree(response);

            // postingUrl is the real apply page — this is what goes in jobUrl column
            String postingUrl = root.has("postingUrl")
                    ? root.get("postingUrl").asText("")
                    : "";

            // Fallback if postingUrl somehow missing
            if (postingUrl.isBlank()) {
                postingUrl = "https://jobs.smartrecruiters.com/" + companyIdentifier + "/" + jobId;
                LOGGER.warn("postingUrl missing for jobId {}, using fallback URL", jobId);
            }

            // Build description from structured sections
            JsonNode sections = root.path("jobAd").path("sections");
            StringBuilder sb = new StringBuilder();
            appendSection(sb, sections, "companyDescription");
            appendSection(sb, sections, "jobDescription");
            appendSection(sb, sections, "qualifications");
            appendSection(sb, sections, "additionalInformation");

            return SmartRecruitersJobDetailDTO.builder()
                    .postingUrl(postingUrl)
                    .description(sb.toString().trim())
                    .build();

        } catch (Exception e) {
            LOGGER.warn("Failed to fetch job detail for jobId {}: {}", jobId, e.getMessage());
            // Return fallback URL so job isn't lost entirely
            return SmartRecruitersJobDetailDTO.builder()
                    .postingUrl("https://jobs.smartrecruiters.com/" + companyIdentifier + "/" + jobId)
                    .build();
        }
    }

    private void appendSection(StringBuilder sb, JsonNode sections, String sectionName) {
        JsonNode section = sections.path(sectionName);
        if (!section.isMissingNode() && section.has("text")) {
            String text = section.get("text").asText("");
            if (!text.isBlank()) {
                sb.append(text).append("\n\n");
            }
        }
    }

}
