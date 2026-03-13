package org.api.jobassist.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.api.jobassist.entity.AtsPlatform;
import org.api.jobassist.entity.Company;
import org.api.jobassist.entity.JobPosting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class WorkdayCrawler implements AtsCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkdayCrawler.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<JobPosting> crawl(String atsJobUrl, Company company, AtsPlatform platform) {
        List<JobPosting> jobs = new ArrayList<>();
        String apiUrl = null;
        try {
            apiUrl = atsJobUrl;
            int offset = 0;
            int limit = 20;

            while (true) {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("limit", limit);
                requestBody.put("offset", offset);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

                ResponseEntity<String> response =
                        restTemplate.postForEntity(
                                atsJobUrl,
                                request,
                                String.class
                        );

                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode jobArray = root.get("jobPostings");

                if (jobArray == null || !jobArray.isArray() || jobArray.size() == 0) {
                    break;
                }

                for (JsonNode jobNode : jobArray) {
                    String title = jobNode.get("title").asText();
                    String jobUrl = jobNode.get("externalPath").asText();

                    String location = "";
                    if (jobNode.has("locationsText")) {
                        location = jobNode.get("locationsText").asText();
                    }

                    JobPosting job = JobPosting.builder()
                            .active(true)
                            .jobTitle(title)
                            .jobUrl(atsJobUrl + jobUrl)
                            .location(location)
                            .company(company)
                            .atsPlatform(platform)
                            .jobDescription("")
                            .postedDate(LocalDate.now())
                            .lastSeenAt(LocalDateTime.now())
                            .firstSeenAt(LocalDateTime.now())
                            .build();

                    jobs.add(job);
                }

                offset += limit;

                if (jobArray.size() < limit) {
                    break;
                }
            }

        } catch (HttpClientErrorException.NotFound e) {
            LOGGER.warn("Workday endpoint not found (404): {} -> {}", apiUrl, e.getMessage());
        } catch (HttpClientErrorException.BadRequest e) {
            LOGGER.warn("Bad request to Workday API (400): {} -> {}", apiUrl, e.getMessage());
        } catch (HttpClientErrorException e) {
            LOGGER.warn("Client error calling Workday API {}: {} -> {}", apiUrl, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.warn("Server error calling Workday API {}: {} -> {}", apiUrl, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            LOGGER.warn("I/O error calling Workday API {}: {}", apiUrl, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error crawling Workday {}: {}", apiUrl, e.getMessage(), e);
        }
        return jobs;
    }

}
