package kakaotech.bootcamp.respec.specranking.domain.spec.controller;

import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.RankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
public class SpecRankingController {

    private final SpecGetService specRankingService;

    @GetMapping(params = "type=ranking")
    public RankingResponse getRankings(
            @RequestParam(value = "jobField", required = false) String jobField,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        try {
            return specRankingService.getRankings(jobField, cursor, limit);
        } catch (Exception e) {
            e.printStackTrace();
            return RankingResponse.fail("서버 오류");
        }
    }
}