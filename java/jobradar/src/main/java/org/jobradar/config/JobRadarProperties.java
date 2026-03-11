package org.jobradar.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jobradar.scoring")
public class JobRadarProperties {

    @Value("${jobradar.scoring.threshold:0.4}")
    private double threshold;

}
