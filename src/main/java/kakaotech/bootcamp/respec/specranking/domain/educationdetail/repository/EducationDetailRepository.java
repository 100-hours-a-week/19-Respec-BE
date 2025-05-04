package kakaotech.bootcamp.respec.specranking.domain.educationdetail.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.educationdetail.entity.EducationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationDetailRepository extends JpaRepository<EducationDetail, Long> {
    void deleteByEducationSpecId(Long specId);

    List<EducationDetail> findByEducationId(Long educationId);
}
