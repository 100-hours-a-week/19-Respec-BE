package kakaotech.bootcamp.respec.specranking.domain.bookmark.repository;

import java.util.Set;
import kakaotech.bootcamp.respec.specranking.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT b.spec.id FROM Bookmark b WHERE b.user.id = :userId")
    Set<Long> findSpecIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.spec.id = :specId")
    Long countBySpecId(@Param("specId") Long specId);

}
