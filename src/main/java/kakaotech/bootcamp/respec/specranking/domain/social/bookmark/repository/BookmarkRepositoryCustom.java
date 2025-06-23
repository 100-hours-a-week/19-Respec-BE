package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository;

import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.entity.Bookmark;

import java.util.List;

public interface BookmarkRepositoryCustom {

    List<Bookmark> findBookmarksByUserIdWithCursor(Long userId, Long cursorId, int limit);
}
