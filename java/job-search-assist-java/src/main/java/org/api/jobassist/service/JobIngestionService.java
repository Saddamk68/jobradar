package org.api.jobassist.service;

import lombok.RequiredArgsConstructor;
import org.api.jobassist.entity.CompanyAts;
import org.api.jobassist.repository.CompanyAtsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JobIngestionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobIngestionService.class);

    private final CompanyAtsRepository companyAtsRepository;
    private final CompanyIngestionService companyIngestionService;

    public void ingestJobs() {

        List<CompanyAts> mappings = companyAtsRepository.findActiveWithAssociations();

        int threadPoolSize = Math.min(5, mappings.size()); // max 5 parallel companies

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        for (CompanyAts mapping : mappings) {
            executor.submit(() -> {
                try {
                    companyIngestionService.ingestCompany(mapping);
                } catch (Exception e) {
                    LOGGER.error("Failed ingestion for company: {}",
                            mapping.getCompany().getName(), e);
                }
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        LOGGER.info("Job ingestion cycle completed.");
    }

}
