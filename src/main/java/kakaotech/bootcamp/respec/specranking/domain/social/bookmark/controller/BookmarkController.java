package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.controller;

import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.dto.BookmarkCreateResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.dto.BookmarkListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.service.BookmarkQueryService;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.service.BookmarkService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final BookmarkQueryService bookmarkqueryService;

    @GetMapping
    public BookmarkListResponse getBookmarkList(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return bookmarkqueryService.getBookmarkList(cursor, limit);
    }

    @PostMapping("/specs/{specId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BookmarkCreateResponse createBookmark(@PathVariable Long specId) {
        Long bookmarkId = bookmarkService.createBookmark(specId);
        return new BookmarkCreateResponse(true, "즐겨찾기 등록 성공", bookmarkId);
    }

    @DeleteMapping("/specs/{specId}")
    public SimpleResponseDto deleteBookmark(@PathVariable Long specId) {
        bookmarkService.deleteBookmark(specId);
        return new SimpleResponseDto(true, "즐겨찾기 해제 성공");
    }
}
