package kakaotech.bootcamp.respec.specranking.domain.certification.repository;

import kakaotech.bootcamp.respec.specranking.domain.certification.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    void deleteBySpecId(Long specId);
}
