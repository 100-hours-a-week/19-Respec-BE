package kakaotech.bootcamp.respec.specranking.domain.spec.controller;

import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.RankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SearchResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
public class SpecQueryController {

    private final SpecQueryService specQueryService;

    @GetMapping(params = "type=ranking")
    public RankingResponse getRankings(
            @RequestParam(value = "jobField", required = false) String jobField,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        return specQueryService.getRankings(jobField, cursor, limit);
    }

    @GetMapping(params = "type=search")
    public SearchResponse searchSpecs(
            @RequestParam(value = "nickname-keyword") String keyword,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        return specQueryService.searchByNickname(keyword, cursor, limit);
    }
}
