package kakaotech.bootcamp.respec.specranking.domain.bookmark.controller;

import kakaotech.bootcamp.respec.specranking.domain.bookmark.dto.BookmarkListResponse;
import kakaotech.bootcamp.respec.specranking.domain.bookmark.service.BookmarkQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkQueryService bookmarkqueryService;

    @GetMapping
    public BookmarkListResponse getBookmarkList(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return bookmarkqueryService.getBookmarkList(cursor, limit);
    }
}
