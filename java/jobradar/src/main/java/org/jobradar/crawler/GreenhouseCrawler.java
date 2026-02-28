package org.jobradar.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jobradar.entity.AtsPlatform;
import org.jobradar.entity.Company;
import org.jobradar.entity.JobPosting;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class GreenhouseCrawler implements AtsCrawler {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<JobPosting> crawl(String atsJobUrl, Company company, AtsPlatform platform) {

        List<JobPosting> jobs = new ArrayList<>();

        try {

            // Extract board name from URL
            String boardName = atsJobUrl.substring(atsJobUrl.lastIndexOf("/") + 1);

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
                String description = jobNode.has("content")
                        ? jobNode.get("content").asText()
                        : "";

                JobPosting job = JobPosting.builder()
                        .company(company)
                        .atsPlatform(platform)
                        .jobTitle(title)
                        .jobUrl(jobUrl)
                        .jobDescription(description)
                        .postedDate(LocalDate.now())
                        .active(true)
                        .build();

                jobs.add(job);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobs;
    }
}