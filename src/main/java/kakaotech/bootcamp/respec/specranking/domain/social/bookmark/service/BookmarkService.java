package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.service;

import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.entity.Bookmark;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
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

    public Long createBookmark(Long specId) {
        Long currentUserId = getCurrentUserIdOrThrow();
        User currentUser = findUserById(currentUserId);
        Spec targetSpec = findSpecById(specId);

        validateNotSelfBookmark(targetSpec, currentUserId);
        validateBookmarkNotExists(specId, currentUserId);

        Bookmark bookmark = new Bookmark(targetSpec, currentUser);

        return bookmarkRepository.save(bookmark).getId();
    }

    public void deleteBookmark(Long specId) {
        Long currentUserId = getCurrentUserIdOrThrow();
        validateUserExists(currentUserId);
        validateSpecExists(specId);

        Bookmark bookmark = findBookmarkBySpecIdAndUserId(specId, currentUserId);
        bookmarkRepository.delete(bookmark);
    }

    private Long getCurrentUserIdOrThrow() {
        return UserUtils.getCurrentUserId()
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Spec findSpecById(Long specId) {
        return specRepository.findById(specId)
                .orElseThrow(() -> new CustomException(ErrorCode.SPEC_NOT_FOUND));
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private void validateSpecExists(Long specId) {
        if (!specRepository.existsById(specId)) {
            throw new CustomException(ErrorCode.SPEC_NOT_FOUND);
        }
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
