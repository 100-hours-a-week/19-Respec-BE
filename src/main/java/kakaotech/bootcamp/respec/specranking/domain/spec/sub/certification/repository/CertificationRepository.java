package kakaotech.bootcamp.respec.specranking.domain.spec.sub.certification.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.spec.sub.certification.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findBySpecId(Long specId);
}
