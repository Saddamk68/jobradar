package org.jobradar.service;

import lombok.RequiredArgsConstructor;
import org.jobradar.entity.JobPosting;
import org.jobradar.entity.TargetSkill;
import org.jobradar.repository.TargetSkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobScoringService {

    private final TargetSkillRepository targetSkillRepository;

    public double calculateMatchScore(JobPosting job) {

        List<TargetSkill> skills = targetSkillRepository.findByActiveTrue();

        if (job.getJobDescription() == null) {
            return 0.0;
        }

        String description = job.getJobDescription().toLowerCase();

        double totalWeight = skills.stream()
                .mapToDouble(TargetSkill::getWeight)
                .sum();

        double matchedWeight = skills.stream()
                .filter(skill ->
                        description.contains(skill.getSkillName().toLowerCase()))
                .mapToDouble(TargetSkill::getWeight)
                .sum();

        if (totalWeight == 0) {
            return 0.0;
        }

        return matchedWeight / totalWeight;
    }
}
