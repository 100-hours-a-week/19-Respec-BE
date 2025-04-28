package kakaotech.bootcamp.respec.specranking.domain.bookmark.repository;

import kakaotech.bootcamp.respec.specranking.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
