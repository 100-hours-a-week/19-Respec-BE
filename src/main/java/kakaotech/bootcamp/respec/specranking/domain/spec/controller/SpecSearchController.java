package kakaotech.bootcamp.respec.specranking.domain.spec.controller;

import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SearchResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
public class SpecSearchController {

    private final SpecSearchService specSearchService;

    @GetMapping(params = "type=search")
    public SearchResponse searchSpecs(
            @RequestParam(value = "nickname-keyword") String keyword,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return SearchResponse.fail("검색어는 필수 입력 항목입니다.");
        }
        
        try {
            return specSearchService.searchByNickname(keyword, cursor, limit);
        } catch (Exception e) {
            e.printStackTrace();
            return SearchResponse.fail("서버 오류");
        }
    }
}
