package org.jobradar.repository;

import org.jobradar.entity.JobAnalysis;
import org.jobradar.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobAnalysisRepository extends JpaRepository<JobAnalysis, Long> {

    Optional<JobAnalysis> findByJob(JobPosting job);

    @Query("""
            SELECT ja FROM JobAnalysis ja
            JOIN FETCH ja.job j
            JOIN FETCH j.company
            WHERE ja.matchScore >= :minScore
            ORDER BY ja.matchScore DESC
            """)
    List<JobAnalysis> findTopByMinScore(@Param("minScore") double minScore);

    @Query("""
            SELECT ja FROM JobAnalysis ja
            JOIN FETCH ja.job j
            JOIN FETCH j.company
            WHERE j.active = true
            AND j.firstSeenAt >= :since
            AND j.lastRecommendedAt IS NULL
            AND ja.matchScore >= :minScore
            ORDER BY ja.matchScore DESC
            """)
    List<JobAnalysis> findRecentUnnotifiedAboveScore(
            @Param("since") LocalDateTime since,
            @Param("minScore") double minScore);

}
