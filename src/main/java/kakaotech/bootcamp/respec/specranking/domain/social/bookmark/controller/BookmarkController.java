package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.controller;

import jakarta.validation.constraints.Positive;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.dto.BookmarkCreateResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.dto.BookmarkListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.service.BookmarkQueryService;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.service.BookmarkService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private static final String DEFAULT_LIMIT = "10";

    private final BookmarkService bookmarkService;
    private final BookmarkQueryService bookmarkQueryService;

    @GetMapping
    public BookmarkListResponse getBookmarkList(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) int limit) {
        return bookmarkQueryService.getBookmarkList(cursor, limit);
    }

    @PostMapping("/specs/{specId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BookmarkCreateResponse createBookmark(
            @PathVariable
            @Positive(message = "specId는 양수여야 합니다.")
            Long specId) {
        return bookmarkService.createBookmark(specId);
    }

    @DeleteMapping("/specs/{specId}")
    public SimpleResponseDto deleteBookmark(
            @PathVariable
            @Positive(message = "specId는 양수여야 합니다.")
            Long specId) {
        return bookmarkService.deleteBookmark(specId);
    }
}
