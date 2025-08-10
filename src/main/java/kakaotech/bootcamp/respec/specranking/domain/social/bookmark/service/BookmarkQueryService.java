package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.service;

import java.util.Base64;
import java.util.List;

import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.constants.BookmarkMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.dto.BookmarkListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.entity.Bookmark;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkQueryService {

    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;
    private final SpecRepository specRepository;
    private final UserRepository userRepository;

    public BookmarkListResponse getBookmarkList(String cursor, int limit) {
        User user = getCurrentUser();

        Long decodedCursorId = decodeCursor(cursor);
        List<Bookmark> bookmarks = bookmarkRepository.findBookmarksByUserIdWithCursor(user.getId(), decodedCursorId, limit + 1);

        BookmarkPaginationResult paginationResult = createPagination(bookmarks, limit);
        List<BookmarkListResponse.BookmarkItem> bookmarkItems = buildBookmarkItems(paginationResult.bookmarks());

        return BookmarkListResponse.success(
                bookmarkItems,
                paginationResult.hasNext(),
                paginationResult.nextCursor(),
                BookmarkMessages.GET_BOOKMARK_LIST_SUCCESS
        );
    }

    private User getCurrentUser() {
        Long currentUserId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private BookmarkPaginationResult createPagination(List<Bookmark> bookmarks, int limit) {
        boolean hasNext = bookmarks.size() > limit;
        String nextCursor = null;

        if (hasNext) {
            bookmarks = bookmarks.subList(0, limit);
            nextCursor = encodeCursor(bookmarks.getLast().getId());
        }

        return new BookmarkPaginationResult(bookmarks, hasNext, nextCursor);
    }

    private List<BookmarkListResponse.BookmarkItem> buildBookmarkItems(List<Bookmark> bookmarks) {
        return bookmarks.stream().map(this::buildBookmarkItem).toList();
    }

    private BookmarkListResponse.BookmarkItem buildBookmarkItem(Bookmark bookmark) {
        Spec spec = bookmark.getSpec();
        BookmarkListResponse.SpecInfo specInfo = buildSpecInfo(spec);
        return new BookmarkListResponse.BookmarkItem(bookmark.getId(), specInfo);
    }

    private BookmarkListResponse.SpecInfo buildSpecInfo(Spec spec) {
        User specOwner = spec.getUser();
        JobField jobField = spec.getJobField();
        Double score = spec.getTotalAnalysisScore();

        Long totalRank = specRepository.findAbsoluteRankByJobField(JobField.TOTAL, spec.getId());
        Long totalUserCount = userRepository.countUsersHavingSpec();
        Long jobFieldRank = specRepository.findAbsoluteRankByJobField(jobField, spec.getId());
        Long jobFieldUserCount = specRepository.countByJobField(jobField);

        Long commentsCount = commentRepository.countBySpecId(spec.getId());
        Long bookmarksCount = bookmarkRepository.countBySpecId(spec.getId());

        return new BookmarkListResponse.SpecInfo(
                spec.getId(), specOwner.getId(), specOwner.getNickname(), specOwner.getUserProfileUrl(),
                score, totalRank, totalUserCount, jobFieldRank, jobFieldUserCount, jobField,
                true, commentsCount, bookmarksCount
        );
    }

    private String encodeCursor(Long cursorId) {
        if (cursorId == null) return null;
        return Base64.getEncoder().encodeToString(String.valueOf(cursorId).getBytes());
    }

    private Long decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) return Long.MAX_VALUE;

        byte[] decodedBytes = Base64.getDecoder().decode(cursor);
        String decodedString = new String(decodedBytes);
        return Long.parseLong(decodedString);
    }

    private record BookmarkPaginationResult(
            List<Bookmark> bookmarks,
            boolean hasNext,
            String nextCursor
    ) {}
}
