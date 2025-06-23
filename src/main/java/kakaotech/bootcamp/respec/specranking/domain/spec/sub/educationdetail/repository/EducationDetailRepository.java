package kakaotech.bootcamp.respec.specranking.domain.spec.sub.educationdetail.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.educationdetail.entity.EducationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationDetailRepository extends JpaRepository<EducationDetail, Long> {
    List<EducationDetail> findByEducationId(Long educationId);
}
