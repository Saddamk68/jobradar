package org.api.jobassist.repository;

import org.api.jobassist.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByActiveTrue();

    Optional<Company> findByNameIgnoreCase(String name);

}
