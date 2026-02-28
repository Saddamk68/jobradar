package org.jobradar.repository;

import org.jobradar.entity.JobAnalysis;
import org.jobradar.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobAnalysisRepository extends JpaRepository<JobAnalysis, Long> {

    Optional<JobAnalysis> findByJob(JobPosting job);

}
