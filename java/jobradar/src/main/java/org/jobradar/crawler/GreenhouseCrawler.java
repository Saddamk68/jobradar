package org.jobradar.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jobradar.entity.AtsPlatform;
import org.jobradar.entity.Company;
import org.jobradar.entity.JobPosting;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GreenhouseCrawler implements AtsCrawler {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<JobPosting> crawl(String atsJobUrl, Company company, AtsPlatform platform) {

        List<JobPosting> jobs = new ArrayList<>();

        try {
            // normalize URL and trim trailing slash, then extract board name
            String normalizedUrl = atsJobUrl == null ? "" : atsJobUrl;
            if (normalizedUrl.endsWith("/")) {
                normalizedUrl = normalizedUrl.substring(0, normalizedUrl.length() - 1);
            }
            // Extract board name from URL
            String boardName = normalizedUrl.substring(normalizedUrl.lastIndexOf("/") + 1);

            String apiUrl = "https://boards-api.greenhouse.io/v1/boards/"
                    + boardName
                    + "/jobs?content=true";

            String response = restTemplate.getForObject(apiUrl, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode jobsArray = root.get("jobs");

            if (jobsArray == null || !jobsArray.isArray()) {
                return jobs;
            }

            for (JsonNode jobNode : jobsArray) {

                String title = jobNode.get("title").asText();
                String jobUrl = jobNode.get("absolute_url").asText();
                JsonNode locationNode = jobNode.get("location");
                String location = locationNode != null && locationNode.has("name")
                        ? locationNode.get("name").asText()
                        : "";
                String description = jobNode.has("content")
                        ? jobNode.get("content").asText()
                        : "";

                String updatedAtRaw = jobNode.has("updated_at")
                        ? jobNode.get("updated_at").asText()
                        : null;

                OffsetDateTime updatedAt = null;

                if (updatedAtRaw != null) {
                    updatedAt = OffsetDateTime.parse(updatedAtRaw);
                }

                JobPosting job = JobPosting.builder()
                        .active(true)
                        .location(location)
                        .company(company)
                        .atsPlatform(platform)
                        .jobTitle(title)
                        .jobUrl(jobUrl)
                        .jobDescription(description)
                        .postedDate(updatedAt != null ? updatedAt.toLocalDate() : LocalDate.now())
                        .firstSeenAt(LocalDateTime.now())
                        .lastSeenAt(updatedAt != null ? updatedAt.toLocalDateTime() : LocalDateTime.now())
                        .build();

                jobs.add(job);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobs;
    }
}