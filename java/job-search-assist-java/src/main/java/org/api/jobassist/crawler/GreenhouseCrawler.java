package org.api.jobassist.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
public class GreenhouseCrawler implements AtsCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreenhouseCrawler.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<JobPosting> crawl(String atsJobUrl, Company company, AtsPlatform platform) {
        List<JobPosting> jobs = new ArrayList<>();

        String apiUrl = null;
        try {
            // normalize URL and trim trailing slash, then extract board name
            String normalizedUrl = atsJobUrl == null ? "" : atsJobUrl;
            if (normalizedUrl.endsWith("/")) {
                normalizedUrl = normalizedUrl.substring(0, normalizedUrl.length() - 1);
            }
            // Extract board name from URL
            String boardName = normalizedUrl.substring(normalizedUrl.lastIndexOf("/") + 1);

            apiUrl = "https://boards-api.greenhouse.io/v1/boards/"
                    + boardName
                    + "/jobs?content=true";

            String response = restTemplate.getForObject(apiUrl, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode jobsArray = root.get("jobs");

            if (jobsArray == null || !jobsArray.isArray()) {
                return jobs;
            }

            for (JsonNode jobNode : jobsArray) {
                // Filter non-English jobs
                String language = jobNode.has("language")
                        ? jobNode.get("language").get("name").asText("")
                        : "";
                if (!language.isBlank() && !language.equalsIgnoreCase("English")) {
                    LOGGER.debug("Skipping non-English job: {} [{}]", jobNode.get("title").asText(), language);
                    continue;
                }

                String title = jobNode.get("title").asText();
                String jobUrl = jobNode.get("absolute_url").asText();
                JsonNode locationNode = jobNode.get("location");

                String location = locationNode != null && locationNode.has("name")
                        ? locationNode.get("name").asText()
                        : "";
                String description = jobNode.has("content")
                        ? jobNode.get("content").asText()
                        : "";
                String publishedAtRaw = jobNode.has("first_published")
                        ? jobNode.get("first_published").asText()
                        : null;
                String updatedAtRaw = jobNode.has("updated_at")
                        ? jobNode.get("updated_at").asText()
                        : null;

                LocalDate postedDate = null;
                LocalDateTime lastSeenAt = LocalDateTime.now();

                // first_published_at is the actual job post date — prefer it
                if (publishedAtRaw != null && !publishedAtRaw.isBlank()) {
                    try {
                        postedDate = OffsetDateTime.parse(publishedAtRaw).toLocalDate();
                    } catch (Exception e) {
                        LOGGER.warn("Failed to parse first_published_at: {}", publishedAtRaw);
                    }
                }

                // updated_at for lastSeenAt tracking
                if (updatedAtRaw != null && !updatedAtRaw.isBlank()) {
                    try {
                        lastSeenAt = OffsetDateTime.parse(updatedAtRaw).toLocalDateTime();
                    } catch (Exception e) {
                        LOGGER.warn("Failed to parse updated_at: {}", updatedAtRaw);
                    }
                }

                JobPosting job = JobPosting.builder()
                        .active(true)
                        .location(location)
                        .company(company)
                        .atsPlatform(platform)
                        .jobTitle(title)
                        .jobUrl(jobUrl)
                        .jobDescription(description)
                        .postedDate(postedDate)          // null if not available — don't fake it
                        .firstSeenAt(LocalDateTime.now())
                        .lastSeenAt(lastSeenAt)
                        .build();

                jobs.add(job);
            }

        } catch (HttpClientErrorException.NotFound e) {
            LOGGER.warn("Greenhouse board not found (404): {} -> {}", apiUrl, e.getMessage());
        } catch (HttpClientErrorException.BadRequest e) {
            LOGGER.warn("Bad request to Greenhouse API (400): {} -> {}", apiUrl, e.getMessage());
        } catch (HttpClientErrorException e) {
            LOGGER.warn("Client error calling Greenhouse API {}: {} -> {}", apiUrl, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.warn("Server error calling Greenhouse API {}: {} -> {}", apiUrl, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            LOGGER.warn("I/O error calling Greenhouse API {}: {}", apiUrl, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error crawling Greenhouse {}: {}", apiUrl, e.getMessage(), e);
        }

        return jobs;
    }
}