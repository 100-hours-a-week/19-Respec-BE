package kakaotech.bootcamp.respec.specranking.domain.comment.repository;

import kakaotech.bootcamp.respec.specranking.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
