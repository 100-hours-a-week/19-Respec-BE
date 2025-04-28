package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import kakaotech.bootcamp.respec.specranking.domain.spec.entity.EducationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationDetailRepository extends JpaRepository<EducationDetail, Long> {
}
