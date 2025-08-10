package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.service;

import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.constants.BookmarkMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.dto.BookmarkCreateResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.entity.Bookmark;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {

    private final UserRepository userRepository;
    private final SpecRepository specRepository;
    private final BookmarkRepository bookmarkRepository;

    public BookmarkCreateResponse createBookmark(Long specId) {
        User currentUser = getCurrentUser();
        Spec targetSpec = findSpecById(specId);

        validateNotSelfBookmark(targetSpec, currentUser.getId());
        validateBookmarkNotExists(specId, currentUser.getId());

        Bookmark bookmark = new Bookmark(targetSpec, currentUser);
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        return BookmarkCreateResponse.success(
                BookmarkMessages.BOOKMARK_CREATE_SUCCESS,
                savedBookmark.getId()
        );
    }

    public SimpleResponseDto deleteBookmark(Long specId) {
        User currentUser = getCurrentUser();
        Spec targetSpec = findSpecById(specId);

        Bookmark bookmark = findBookmarkBySpecIdAndUserId(targetSpec.getId(), currentUser.getId());
        bookmarkRepository.delete(bookmark);

        return SimpleResponseDto.success(BookmarkMessages.BOOKMARK_DELETE_SUCCESS);
    }

    private User getCurrentUser() {
        Long currentUserId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private Spec findSpecById(Long specId) {
        return specRepository.findById(specId)
                .orElseThrow(() -> new CustomException(ErrorCode.SPEC_NOT_FOUND));
    }

    private Bookmark findBookmarkBySpecIdAndUserId(Long specId, Long userId) {
        return bookmarkRepository.findBySpecIdAndUserId(specId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));
    }

    private void validateNotSelfBookmark(Spec spec, Long userId) {
        if (spec.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.SELF_BOOKMARK_NOT_ALLOWED);
        }
    }

    private void validateBookmarkNotExists(Long specId, Long userId) {
        if (bookmarkRepository.existsBySpecIdAndUserId(specId, userId)) {
            throw new CustomException(ErrorCode.BOOKMARK_ALREADY_EXISTS);
        }
    }
}
