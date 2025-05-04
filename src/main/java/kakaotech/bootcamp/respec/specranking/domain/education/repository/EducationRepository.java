package kakaotech.bootcamp.respec.specranking.domain.education.repository;

import kakaotech.bootcamp.respec.specranking.domain.education.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<Education, Long> {
    void deleteBySpecId(Long specId);

    Education findBySpecId(Long specId);
}
