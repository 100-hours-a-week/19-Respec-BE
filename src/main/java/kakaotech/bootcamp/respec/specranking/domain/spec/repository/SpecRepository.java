package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecRepository extends JpaRepository<Spec, Long> {
}
