package kakaotech.bootcamp.respec.specranking.domain.spec.controller;

import kakaotech.bootcamp.respec.specranking.domain.spec.dto.request.PostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.PostSpecResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.RankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecGetService;
import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecPostPutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
public class SpecController {

    private final SpecPostPutService specPostPutService;
    private final SpecGetService specGetService;

    @PostMapping
    public PostSpecResponse createSpec(
            @RequestPart("spec") PostSpecRequest request,
            @RequestPart(value = "portfolioFile", required = false) MultipartFile portfolioFile) {

        specPostPutService.createSpec(request, portfolioFile);
        return new PostSpecResponse(true, "스펙 입력 성공!");
    }

    @PutMapping("/{specId}")
    public PostSpecResponse updateSpec(
            @PathVariable Long specId,
            @RequestPart("spec") PostSpecRequest request,
            @RequestPart(value = "portfolioFile", required = false) MultipartFile portfolioFile) {

        specPostPutService.updateSpec(specId, request, portfolioFile);
        return new PostSpecResponse(true, "스펙 수정 성공!");
    }

    @GetMapping
    public RankingResponse getRankings(
            @RequestParam(value = "type", defaultValue = "ranking") String type,
            @RequestParam(value = "jobField", required = false) String jobField,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        if (!"ranking".equals(type)) {
            return RankingResponse.fail("유효하지 않은 직무명");
        }

        try {
            return specGetService.getRankings(jobField, cursor, limit);
        } catch (Exception e) {
            e.printStackTrace();
            return RankingResponse.fail("서버 오류");
        }
    }
}