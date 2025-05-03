package kakaotech.bootcamp.respec.specranking.domain.englishskill.repository;

import kakaotech.bootcamp.respec.specranking.domain.englishskill.entity.LanguageSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnglishSkillRepository extends JpaRepository<LanguageSkill, Long> {
    void deleteBySpecId(Long specId);
}
