package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import kakaotech.bootcamp.respec.specranking.domain.spec.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    void deleteBySpecId(Long specId);
}
