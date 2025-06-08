package kakaotech.bootcamp.respec.specranking.domain.spec.service;

import kakaotech.bootcamp.respec.specranking.domain.bookmark.entity.Bookmark;
import kakaotech.bootcamp.respec.specranking.domain.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SpecBookmarkService {

    private final UserRepository userRepository;
    private final SpecRepository specRepository;
    private final BookmarkRepository bookmarkRepository;

    public Long createBookmark(Long specId) {
        Optional<Long> optUserId = UserUtils.getCurrentUserId();
        Long userId = optUserId.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Spec spec = specRepository.findById(specId)
                .orElseThrow(() -> new IllegalArgumentException("스펙을 찾을 수 없습니다. ID: " + specId));

        validateSelfBookmark(spec, userId);
        validateDuplicateBookmark(specId, userId);

        Bookmark bookmark = new Bookmark(spec, user);
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        return savedBookmark.getId();
    }

    public void deleteBookmark(Long specId, Long bookmarkId) {
        Optional<Long> optUserId = UserUtils.getCurrentUserId();
        Long userId = optUserId.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
        specRepository.findById(specId).orElseThrow(() -> new IllegalArgumentException("스펙을 찾을 수 없습니다. ID: " + specId));

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 즐겨찾기를 찾을 수 없습니다. ID: " + bookmarkId));

        bookmarkRepository.delete(bookmark);
    }

    private void validateSelfBookmark(Spec spec, Long userId) {
        if (spec.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("자신의 스펙은 즐겨찾기할 수 없습니다.");
        }
    }

    private void validateDuplicateBookmark(Long specId, Long userId) {
        if (bookmarkRepository.existsBySpecIdAndUserId(specId, userId)) {
            throw new IllegalStateException("이미 즐겨찾기에 등록된 스펙입니다.");
        }
    }
}
