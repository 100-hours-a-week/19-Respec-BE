package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecRepository extends JpaRepository<Spec, Long> {
    Optional<Spec> findByUserId(Long userId);
}
