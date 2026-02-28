package org.jobradar.repository;

import org.jobradar.entity.Company;
import org.jobradar.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    boolean existsByJobUrl(String jobUrl);

    Optional<JobPosting> findByJobUrl(String jobUrl);

    List<JobPosting> findByCompanyAndActiveTrue(Company company);

    List<JobPosting> findByActiveTrueAndFirstSeenAtAfter(LocalDateTime dateTime);

    List<JobPosting> findByActiveTrueAndFirstSeenAtAfterAndLastRecommendedAtIsNull(LocalDateTime dateTime);

}
