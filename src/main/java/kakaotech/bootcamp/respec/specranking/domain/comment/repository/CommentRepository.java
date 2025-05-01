package kakaotech.bootcamp.respec.specranking.domain.comment.repository;

import java.util.List;

import kakaotech.bootcamp.respec.specranking.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.spec.id = :specId")
    int countBySpecId(@Param("specId") Long specId);
    
    List<Comment> findBySpecId(Long specId);
}
