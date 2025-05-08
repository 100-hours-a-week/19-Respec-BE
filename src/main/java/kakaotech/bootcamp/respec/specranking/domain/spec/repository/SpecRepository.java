package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import java.util.Optional;

import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecRepository extends JpaRepository<Spec, Long>, SpecRepositoryCustom {
    Optional<Spec> findByUserId(Long userId);

    Optional<Spec> findByUserIdAndStatus(Long userId, SpecStatus status);
}
