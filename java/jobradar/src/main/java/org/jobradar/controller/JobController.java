package org.jobradar.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobradar.dto.JobAnalysisResponseDTO;
import org.jobradar.service.JobAlertService;
import org.jobradar.service.JobQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class JobController {

    private final JobQueryService jobQueryService;
    private final JobAlertService jobAlertService;

    @GetMapping("/jobs")
    public ResponseEntity<List<JobAnalysisResponseDTO>> getTopJobs(
            @RequestParam(name = "minScore", defaultValue = "0.4") double minScore) {
        return ResponseEntity.ok(jobQueryService.getTopJobs(minScore));
    }

    @PostMapping("/alerts/trigger")
    public ResponseEntity<Map<String, String>> triggerDailyDigest() {
        try {
            log.info("Manual trigger of daily digest at {}", LocalDateTime.now());
            jobAlertService.sendDailyDigest();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Daily digest triggered successfully",
                    "triggeredAt", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("Failed to trigger daily digest: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to trigger digest: " + e.getMessage()
                    ));
        }
    }

}
