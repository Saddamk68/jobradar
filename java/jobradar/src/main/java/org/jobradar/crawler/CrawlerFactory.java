package org.jobradar.crawler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlerFactory {

    private final GreenhouseCrawler greenhouseCrawler;

    public AtsCrawler getCrawler(String atsName) {

        if ("Greenhouse".equalsIgnoreCase(atsName)) {
            return greenhouseCrawler;
        }

        throw new IllegalArgumentException("No crawler found for ATS: " + atsName);
    }

}
