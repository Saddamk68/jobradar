package org.api.jobassist.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "job-search-assist.scoring")
public class JobSearchProperties {

    @Value("${job-search-assist.scoring.threshold:0.4}")
    private double threshold;

}
