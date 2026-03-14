package org.api.jobassist.scheduler;

import lombok.RequiredArgsConstructor;
import org.api.jobassist.service.JobAlertService;
import org.api.jobassist.service.JobIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);

    private final JobIngestionService jobIngestionService;
    private final JobAlertService jobAlertService;

    // Runs every day at 2:00 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void runDailyJobCrawl() {

        LOGGER.info("Starting scheduled job crawl...");

        jobIngestionService.ingestJobs();
        jobAlertService.sendDailyDigest();

        LOGGER.info("Completed scheduled job crawl.");
    }

}
