package org.jobradar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_posting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ats_platform_id")
    private AtsPlatform atsPlatform;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "job_url", unique = true)
    private String jobUrl;

    @Lob
    @Column(name = "job_description")
    private String jobDescription;

    private String location;

    private LocalDate postedDate;

    @Column(name = "first_seen_at", insertable = false)
    private LocalDateTime firstSeenAt;

    @Column(name = "last_seen_at", insertable = false)
    private LocalDateTime lastSeenAt;

    private Boolean active;

    @Column(name = "last_recommended_at")
    private LocalDateTime lastRecommendedAt;

}
