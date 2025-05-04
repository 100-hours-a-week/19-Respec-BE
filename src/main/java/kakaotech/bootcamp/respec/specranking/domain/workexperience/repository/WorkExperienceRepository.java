package kakaotech.bootcamp.respec.specranking.domain.workexperience.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.workexperience.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    void deleteBySpecId(Long specId);

    List<WorkExperience> findBySpecId(Long specId);
}
