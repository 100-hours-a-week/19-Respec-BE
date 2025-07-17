package kakaotech.bootcamp.respec.specranking.domain.spec.spec.controller;

import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.constant.SpecConstant.META_GET_SUCCESS_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.constant.SpecConstant.RANKING_GET_SUCCESS_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.constant.SpecConstant.SEARCH_LIST_GET_SUCCESS_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.constant.SpecConstant.SPEC_DETAIL_GET_MESSAGE;

import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.RankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.RankingResponse.SpecRankings;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SearchResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SearchResponse.SearchData;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SpecDetailResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SpecDetailResponse.SpecDetail;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SpecMetaResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SpecMetaResponse.Meta;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.query.SpecDetailQueryService;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.query.SpecQueryService;
import kakaotech.bootcamp.respec.specranking.global.common.aop.timetrace.TimeTrace;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
@Slf4j
public class SpecQueryController {

    private final SpecQueryService specQueryService;
    private final SpecDetailQueryService specDetailQueryService;

    @GetMapping(params = "type=ranking")
    @TimeTrace
    public RankingResponse getRankings(
            @RequestParam(value = "jobField") JobField jobField,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        SpecRankings specRankings = specQueryService.getRankings(jobField, cursor, limit);
        return new RankingResponse(true, RANKING_GET_SUCCESS_MESSAGE, specRankings);
    }

    @GetMapping(params = "type=search")
    public SearchResponse searchSpecs(
            @RequestParam(value = "nickname-keyword") String keyword,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        SearchData searchData = specQueryService.searchByNickname(keyword, cursor, limit);
        return new SearchResponse(true, SEARCH_LIST_GET_SUCCESS_MESSAGE, searchData);
    }

    @GetMapping(params = "type=meta")
    public SpecMetaResponse getSpecMeta(@RequestParam(value = "jobField") JobField jobField) {
        Meta metaData = specQueryService.getMetaData(jobField);
        return new SpecMetaResponse(true, META_GET_SUCCESS_MESSAGE, metaData);
    }

    @GetMapping("/{specId}")
    public SpecDetailResponse getSpecDetail(@PathVariable Long specId) {
        log.info("Get Spec Detail Query for Spec ID: {}", specId);
        SpecDetail specDetail = specDetailQueryService.getSpecDetail(specId);
        return new SpecDetailResponse(true, SPEC_DETAIL_GET_MESSAGE, specDetail);
    }

}
