package org.api.jobassist.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api.jobassist.service.JobAlertService;
import org.api.jobassist.service.JobIngestionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobScheduler {

    private final JobIngestionService jobIngestionService;
    private final JobAlertService jobAlertService;

    // Runs every day at 2:00 AM
//    @Scheduled(cron = "0 0 2 * * ?")
    @Scheduled(fixedDelay = 60000)
    public void runDailyJobCrawl() {

        log.info("Starting scheduled job crawl...");

        jobIngestionService.ingestJobs();
        jobAlertService.sendDailyDigest();

        log.info("Completed scheduled job crawl.");
    }

}
