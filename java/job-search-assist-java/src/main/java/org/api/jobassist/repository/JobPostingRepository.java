package org.api.jobassist.repository;

import org.api.jobassist.entity.Company;
import org.api.jobassist.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    boolean existsByJobUrl(String jobUrl);

    Optional<JobPosting> findByJobUrl(String jobUrl);

    @Query("""
                SELECT jp 
                FROM JobPosting jp
                WHERE jp.company = :company
            """)
    List<JobPosting> findAllByCompany(@Param("company") Company company);

    List<JobPosting> findByCompanyAndJobUrlIn(Company company, List<String> jobUrls);

    List<JobPosting> findByCompanyAndActiveTrue(Company company);

    List<JobPosting> findByActiveTrueAndFirstSeenAtAfter(LocalDateTime dateTime);

    List<JobPosting> findByActiveTrueAndFirstSeenAtAfterAndLastRecommendedAtIsNull(LocalDateTime dateTime);

}
