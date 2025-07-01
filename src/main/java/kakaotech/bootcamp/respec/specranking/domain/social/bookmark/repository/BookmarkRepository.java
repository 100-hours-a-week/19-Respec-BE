package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository;

import java.util.List;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {

    @Query("SELECT b.spec.id FROM Bookmark b WHERE b.user.id = :userId")
    List<Long> findSpecIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.spec.id = :specId")
    Long countBySpecId(@Param("specId") Long specId);

    boolean existsBySpecIdAndUserId(Long specId, Long userId);

    Optional<Bookmark> findBySpecIdAndUserId(Long specId, Long userId);

    @Query("SELECT b.spec.id as specId, COUNT(b) as count FROM Bookmark b WHERE b.spec.id IN :specIds GROUP BY b.spec.id")
    List<SpecCountProjection> countBySpecIds(@Param("specIds") List<Long> specIds);

    interface SpecCountProjection {
        Long getSpecId();

        Long getCount();
    }
}
