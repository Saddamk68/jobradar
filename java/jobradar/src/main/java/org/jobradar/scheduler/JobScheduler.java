package org.jobradar.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobradar.service.JobAlertService;
import org.jobradar.service.JobIngestionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobScheduler {

    private final JobIngestionService jobIngestionService;
    private final JobAlertService jobAlertService;

    // Runs once every 24 hours
    @Scheduled(cron = "0 0 2 * * ?")
    public void runDailyJobCrawl() {

        log.info("Starting scheduled job crawl...");

        jobIngestionService.ingestJobs();
        jobAlertService.sendDailyDigest();

        log.info("Completed scheduled job crawl.");
    }

}
