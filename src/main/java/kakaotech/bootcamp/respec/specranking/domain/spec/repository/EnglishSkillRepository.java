package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import kakaotech.bootcamp.respec.specranking.domain.spec.entity.EnglishSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnglishSkillRepository extends JpaRepository<EnglishSkill, Long> {
    void deleteBySpecId(Long specId);
}
