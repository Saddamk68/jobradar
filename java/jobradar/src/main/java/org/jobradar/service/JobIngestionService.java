package org.jobradar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobradar.entity.CompanyAts;
import org.jobradar.repository.CompanyAtsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobIngestionService {

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
                    log.error("Failed ingestion for company: {}",
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

        log.info("Job ingestion cycle completed.");
    }

}
