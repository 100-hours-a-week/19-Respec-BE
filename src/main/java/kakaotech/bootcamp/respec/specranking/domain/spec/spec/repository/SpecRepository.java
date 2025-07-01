package kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository;

import java.util.List;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpecRepository extends JpaRepository<Spec, Long>, SpecRepositoryCustom {
    @Query(value = """
            WITH ranked_specs AS (
                SELECT s.id, 
                       RANK() OVER (ORDER BY s.total_analysis_score DESC) as total_rank,
                       RANK() OVER (PARTITION BY CASE WHEN :jobField = 'TOTAL' THEN NULL ELSE s.job_field END 
                                   ORDER BY s.total_analysis_score DESC) as job_field_rank
                FROM spec s
                WHERE (:jobField = 'TOTAL' OR s.job_field = :jobField)
            )
            SELECT rs.id as specId, 
                   rs.total_rank as totalRank, 
                   rs.job_field_rank as jobFieldRank
            FROM ranked_specs rs
            WHERE rs.id IN :specIds
            """, nativeQuery = true)
    List<SpecRankingProjection> findRankingsBySpecIds(@Param("specIds") List<Long> specIds,
                                                      @Param("jobField") String jobField);

    // Projection 인터페이스
    interface SpecRankingProjection {
        Long getSpecId();

        Long getTotalRank();

        Long getJobFieldRank();
    }

    Long countByStatus(SpecStatus status);

    Optional<Spec> findByUserIdAndStatus(Long userId, SpecStatus status);

    Optional<Spec> findByIdAndStatus(Long id, SpecStatus status);
}
