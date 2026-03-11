package org.jobradar.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jobradar.entity.AtsPlatform;
import org.jobradar.entity.Company;
import org.jobradar.entity.JobPosting;
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
            // Extract company identifier from URL
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
                String title = jobNode.get("name").asText();
                String jobUrl = jobNode.get("ref").asText();
                String location = jobNode.get("location").get("city").asText("");

                String updatedAtRaw = jobNode.has("releasedDate")
                        ? jobNode.get("releasedDate").asText()
                        : null;

                OffsetDateTime updatedAt = null;
                if (updatedAtRaw != null) {
                    updatedAt = OffsetDateTime.parse(updatedAtRaw);
                }

                JobPosting job = JobPosting.builder()
                        .active(true)
                        .jobTitle(title)
                        .jobUrl(jobUrl)
                        .location(location)
                        .company(company)
                        .atsPlatform(platform)
                        .jobDescription("") // SmartRecruiters requires another call for full description
                        .postedDate(updatedAt != null ? updatedAt.toLocalDate() : LocalDate.now())
                        .firstSeenAt(LocalDateTime.now())
                        .lastSeenAt(updatedAt != null ? updatedAt.toLocalDateTime() : LocalDateTime.now())
                        .build();

                jobs.add(job);
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

}
