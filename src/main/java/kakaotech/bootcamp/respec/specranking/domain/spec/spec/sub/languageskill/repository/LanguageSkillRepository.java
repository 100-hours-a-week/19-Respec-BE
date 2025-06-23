package kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.languageskill.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.languageskill.entity.LanguageSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageSkillRepository extends JpaRepository<LanguageSkill, Long> {
    List<LanguageSkill> findBySpecId(Long specId);
}
