package kakaotech.bootcamp.respec.specranking.domain.comment.repository;

import kakaotech.bootcamp.respec.specranking.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.spec.id = :specId")
    Long countBySpecId(@Param("specId") Long specId);

    @Query("SELECT COALESCE(MAX(c.bundle), 0) FROM Comment c WHERE c.spec.id = :specId")
    Integer findMaxBundleBySpecId(@Param("specId") Long specId);

    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.spec.id = :specId")
    Optional<Comment> findByIdAndSpecId(@Param("commentId") Long commentId, @Param("specId") Long specId);
}
