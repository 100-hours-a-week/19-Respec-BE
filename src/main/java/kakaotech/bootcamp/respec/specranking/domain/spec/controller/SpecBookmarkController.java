package kakaotech.bootcamp.respec.specranking.domain.spec.controller;

import kakaotech.bootcamp.respec.specranking.domain.bookmark.dto.BookmarkCreateResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecBookmarkService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
public class SpecBookmarkController {

    private final SpecBookmarkService specBookmarkService;

    @PostMapping("/{specId}/bookmarks")
    @ResponseStatus(HttpStatus.CREATED)
    public BookmarkCreateResponse createBookmark(@PathVariable Long specId) {
        Long bookmarkId = specBookmarkService.createBookmark(specId);
        return new BookmarkCreateResponse(true, "즐겨찾기 등록 성공", bookmarkId);
    }

    @DeleteMapping("/{specId}/bookmarks/{bookmarkId}")
    public SimpleResponseDto deleteBookmark(@PathVariable Long specId, @PathVariable Long bookmarkId) {
        specBookmarkService.deleteBookmark(specId, bookmarkId);
        return new SimpleResponseDto(true, "즐겨찾기 해제 성공");
    }
}
