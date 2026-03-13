package org.api.jobassist.repository;

import org.api.jobassist.entity.AtsPlatform;
import org.api.jobassist.entity.Company;
import org.api.jobassist.entity.CompanyAts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyAtsRepository extends JpaRepository<CompanyAts, Long> {

    List<CompanyAts> findByActiveTrue();

    @Query("""
                SELECT ca 
                FROM CompanyAts ca
                JOIN FETCH ca.company
                JOIN FETCH ca.atsPlatform
                WHERE ca.active = true
            """)
    List<CompanyAts> findActiveWithAssociations();

    boolean existsByCompanyAndAtsPlatform(Company company, AtsPlatform atsPlatform);

}