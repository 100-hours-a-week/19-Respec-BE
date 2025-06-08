package kakaotech.bootcamp.respec.specranking.domain.spec.controller;

import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecBookmarkService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
public class SpecBookmarkController {

    private final SpecBookmarkService specBookmarkService;

    @PostMapping("/{specId}/bookmarks")
    public SimpleResponseDto createBookmark(@PathVariable Long specId) {
        specBookmarkService.createBookmark(specId);
        return new SimpleResponseDto(true, "즐겨찾기 등록 성공");
    }
}
