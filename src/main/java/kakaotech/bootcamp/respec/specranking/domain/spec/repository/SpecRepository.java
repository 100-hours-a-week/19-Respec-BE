package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecRepository extends JpaRepository<Spec, Long>, SpecRepositoryCustom {
    Long countByStatus(SpecStatus status);

    Optional<Spec> findByUserIdAndStatus(Long userId, SpecStatus status);

    Optional<Spec> findByIdAndStatus(Long id, SpecStatus status);
}
