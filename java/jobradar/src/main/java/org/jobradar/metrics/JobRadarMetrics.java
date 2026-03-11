package org.jobradar.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class JobRadarMetrics {

    private final Counter jobsCrawled;
    private final Counter jobsAnalyzed;
    private final Counter jobsSaved;
    private final Counter pythonFailures;
    private final Timer ingestionTimer;

    public JobRadarMetrics(MeterRegistry registry) {

        this.jobsCrawled = Counter.builder("jobradar.jobs.crawled")
                .description("Total jobs crawled")
                .register(registry);

        this.jobsAnalyzed = Counter.builder("jobradar.jobs.analyzed")
                .description("Total jobs sent to Python")
                .register(registry);

        this.jobsSaved = Counter.builder("jobradar.jobs.saved")
                .description("Jobs saved above threshold")
                .register(registry);

        this.pythonFailures = Counter.builder("jobradar.python.failures")
                .description("Python scoring failures")
                .register(registry);

        this.ingestionTimer = Timer.builder("jobradar.ingestion.duration")
                .description("Company ingestion duration")
                .register(registry);
    }

    public void incrementCrawled(int count) {
        jobsCrawled.increment(count);
    }

    public void incrementAnalyzed(int count) {
        jobsAnalyzed.increment(count);
    }

    public void incrementSaved(int count) {
        jobsSaved.increment(count);
    }

    public void incrementPythonFailures() {
        pythonFailures.increment();
    }

    public Timer.Sample startTimer(MeterRegistry registry) {
        return Timer.start(registry);
    }

    public Timer getIngestionTimer() {
        return ingestionTimer;
    }

}
