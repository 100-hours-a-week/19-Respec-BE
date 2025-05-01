package kakaotech.bootcamp.respec.specranking.domain.spec.controller;

import kakaotech.bootcamp.respec.specranking.domain.spec.dto.request.PostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.PostSpecResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
public class SpecController {

    private final SpecService specService;

    @PostMapping
    public PostSpecResponse createSpec(
            @RequestPart("spec") PostSpecRequest request,
            @RequestPart(value = "portfolioFile", required = false) MultipartFile portfolioFile) {

        specService.createSpec(request, portfolioFile);
        return new PostSpecResponse(true, "스펙 입력 성공!");
    }

    @PostMapping("/test")
    public String test(@RequestBody EnumJson enumData) {
        return "OK";
    }

    @Data
    static class EnumJson {
        private EnumData enumData;
    }

    enum EnumData {
        GOOD, BAD
    }
}