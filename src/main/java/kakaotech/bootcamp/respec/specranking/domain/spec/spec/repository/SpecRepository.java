package kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository;

import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpecRepository extends JpaRepository<Spec, Long>, SpecRepositoryCustom {
    Long countByStatus(SpecStatus status);

    Optional<Spec> findByUserIdAndStatus(Long userId, SpecStatus status);

    Optional<Spec> findByIdAndStatus(Long id, SpecStatus status);

    @Query("SELECT COUNT(DISTINCT s.user.id) FROM Spec s")
    long countDistinctUsersHavingSpec();
}
