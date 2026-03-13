package org.api.jobassist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JobSearchAssistApplication {

    public static void main(String[] args) {
        SpringApplication.run(org.api.jobassist.JobSearchAssistApplication.class, args);
    }

}