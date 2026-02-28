package org.jobradar.repository;

import org.jobradar.entity.TargetSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetSkillRepository extends JpaRepository<TargetSkill, Long> {

    List<TargetSkill> findByActiveTrue();

}
