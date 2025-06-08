package kakaotech.bootcamp.respec.specranking.domain.bookmark.repository;

import kakaotech.bootcamp.respec.specranking.domain.bookmark.entity.Bookmark;

import java.util.List;

public interface BookmarkRepositoryCustom {

    List<Bookmark> findBookmarksByUserIdWithCursor(Long userId, Long cursorId, int limit);
}
