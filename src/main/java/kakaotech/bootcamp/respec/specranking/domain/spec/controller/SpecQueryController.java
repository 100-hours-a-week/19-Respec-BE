package kakaotech.bootcamp.respec.specranking.domain.spec.controller;

import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.RankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SearchResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SpecDetailResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecDetailQueryService;
import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
public class SpecQueryController {

    private final SpecQueryService specQueryService;
    private final SpecDetailQueryService specDetailQueryService;

    @GetMapping(params = "type=ranking")
    public RankingResponse getRankings(
            @RequestParam(value = "jobField") JobField jobField,
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

    @GetMapping("/{specId}")
    public SpecDetailResponse getSpecDetail(@PathVariable Long specId) {
        return specDetailQueryService.getSpecDetail(specId);
    }

}
