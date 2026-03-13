package org.api.jobassist.repository;

import org.api.jobassist.entity.TargetSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetSkillRepository extends JpaRepository<TargetSkill, Long> {

    List<TargetSkill> findByActiveTrue();

}
