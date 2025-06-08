package kakaotech.bootcamp.respec.specranking.domain.bookmark.service;

import jakarta.transaction.Transactional;
import kakaotech.bootcamp.respec.specranking.domain.bookmark.dto.BookmarkListResponse;
import kakaotech.bootcamp.respec.specranking.domain.bookmark.entity.Bookmark;
import kakaotech.bootcamp.respec.specranking.domain.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.bookmark.repository.BookmarkRepositoryCustomImpl;
import kakaotech.bootcamp.respec.specranking.domain.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkQueryService {

    private final BookmarkRepositoryCustomImpl bookmarkRepositoryCustomImpl;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;
    private final SpecRepository specRepository;
    private final UserRepository userRepository;

    public BookmarkListResponse getBookmarkList(String cursor, int limit) {
        Optional<Long> optUserId = UserUtils.getCurrentUserId();
        Long userId = optUserId.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Long cursorId = decodeCursor(cursor);
        List<Bookmark> bookmarks = bookmarkRepositoryCustomImpl.findBookmarksByUserIdWithCursor(userId, cursorId, limit + 1);

        boolean hasNext = bookmarks.size() > limit;
        String nextCursor = null;
        if (hasNext) {
            bookmarks = bookmarks.subList(0, limit);
            nextCursor = encodeCursor(bookmarks.getLast().getId());
        }

        List<BookmarkListResponse.BookmarkItem> bookmarkItems = new ArrayList<>();

        for (Bookmark bookmark : bookmarks) {
            Spec spec = bookmark.getSpec();
            User specUser = spec.getUser();
            JobField jobField = spec.getJobField();

            Double score = spec.getTotalAnalysisScore();
            Long totalRank = specRepository.findAbsoluteRankByJobField(JobField.TOTAL, spec.getId());
            Long totalUserCount = userRepository.countUsersHavingSpec();
            Long jobFieldRank = specRepository.findAbsoluteRankByJobField(jobField, spec.getId());
            Long jobFieldUserCount = specRepository.countByJobField(jobField);

            Long commentsCount = commentRepository.countBySpecId(spec.getId());
            Long bookmarksCount = bookmarkRepository.countBySpecId(spec.getId());

            BookmarkListResponse.SpecInfo specInfo = new BookmarkListResponse.SpecInfo(
                    spec.getId(),
                    specUser.getNickname(),
                    specUser.getUserProfileUrl(),
                    score,
                    totalRank,
                    totalUserCount,
                    jobFieldRank,
                    jobFieldUserCount,
                    jobField,
                    true,
                    commentsCount,
                    bookmarksCount
            );

            BookmarkListResponse.BookmarkItem bookmarkItem = new BookmarkListResponse.BookmarkItem(
                    bookmark.getId(),
                    specInfo
            );

            bookmarkItems.add(bookmarkItem);
        }

        return BookmarkListResponse.success(bookmarkItems, hasNext, nextCursor);
    }

    private String encodeCursor(Long cursorId) {
        return Base64.getEncoder().encodeToString(String.valueOf(cursorId).getBytes());
    }

    private Long decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return Long.MAX_VALUE;
        }

        byte[] decodedBytes = Base64.getDecoder().decode(cursor);
        String decodedString = new String(decodedBytes);
        return Long.parseLong(decodedString);
    }
}
