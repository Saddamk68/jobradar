package org.api.jobassist.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class JobSearchMetrics {

    private final Counter jobsCrawled;
    private final Counter jobsAnalyzed;
    private final Counter jobsSaved;
    private final Counter pythonFailures;
    private final Timer ingestionTimer;

    public JobSearchMetrics(MeterRegistry registry) {

        this.jobsCrawled = Counter.builder("jobassist.jobs.crawled")
                .description("Total jobs crawled")
                .register(registry);

        this.jobsAnalyzed = Counter.builder("jobassist.jobs.analyzed")
                .description("Total jobs sent to Python")
                .register(registry);

        this.jobsSaved = Counter.builder("jobassist.jobs.saved")
                .description("Jobs saved above threshold")
                .register(registry);

        this.pythonFailures = Counter.builder("jobassist.python.failures")
                .description("Python scoring failures")
                .register(registry);

        this.ingestionTimer = Timer.builder("jobassist.ingestion.duration")
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
