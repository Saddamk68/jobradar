package org.jobradar.repository;

import org.jobradar.entity.AtsPlatform;
import org.jobradar.entity.Company;
import org.jobradar.entity.CompanyAts;
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