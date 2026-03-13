package org.api.jobassist.crawler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CrawlerFactory {

    private final Map<String, AtsCrawler> crawlers;

    public AtsCrawler getCrawler(String platform) {
        return switch (platform.toLowerCase()) {
            case "greenhouse" -> crawlers.get("greenhouseCrawler");
            case "smartrecruiters" -> crawlers.get("smartRecruitersCrawler");
            case "workday" -> crawlers.get("workdayCrawler");
            default -> throw new IllegalArgumentException("Unsupported ATS: " + platform);
        };
    }

}
