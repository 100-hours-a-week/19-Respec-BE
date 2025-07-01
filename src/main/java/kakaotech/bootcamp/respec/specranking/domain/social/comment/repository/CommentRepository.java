package kakaotech.bootcamp.respec.specranking.domain.social.comment.repository;

import java.util.List;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.spec.id = :specId")
    Long countBySpecId(@Param("specId") Long specId);

    @Query("SELECT COALESCE(MAX(c.bundle), 0) FROM Comment c WHERE c.spec.id = :specId")
    Integer findMaxBundleBySpecId(@Param("specId") Long specId);

    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.spec.id = :specId")
    Optional<Comment> findByIdAndSpecId(@Param("commentId") Long commentId, @Param("specId") Long specId);

    @Query("SELECT c.spec.id as specId, COUNT(c) as count FROM Comment c WHERE c.spec.id IN :specIds GROUP BY c.spec.id")
    List<SpecCountProjection> countBySpecIds(@Param("specIds") List<Long> specIds);

    interface SpecCountProjection {
        Long getSpecId();

        Long getCount();
    }
}
