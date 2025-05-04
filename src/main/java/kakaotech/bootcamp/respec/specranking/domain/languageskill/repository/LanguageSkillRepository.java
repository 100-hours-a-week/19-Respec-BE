package kakaotech.bootcamp.respec.specranking.domain.languageskill.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.languageskill.entity.LanguageSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageSkillRepository extends JpaRepository<LanguageSkill, Long> {
    void deleteBySpecId(Long specId);

    List<LanguageSkill> findBySpecId(Long specId);
}
