package org.jobradar.repository;

import org.jobradar.entity.CompanyAts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyAtsRepository extends JpaRepository<CompanyAts, Long> {

    List<CompanyAts> findByActiveTrue();

}