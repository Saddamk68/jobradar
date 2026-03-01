package org.jobradar.controller;

import lombok.RequiredArgsConstructor;
import org.jobradar.dto.JobAnalysisResponseDTO;
import org.jobradar.service.JobQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final JobQueryService jobQueryService;

    @GetMapping("/api/jobs")
    public ResponseEntity<List<JobAnalysisResponseDTO>> getTopJobs(@RequestParam(name = "minScore", defaultValue = "0.4") double minScore) {
        return new ResponseEntity<>(jobQueryService.getTopJobs(minScore), HttpStatus.OK);
    }

}
