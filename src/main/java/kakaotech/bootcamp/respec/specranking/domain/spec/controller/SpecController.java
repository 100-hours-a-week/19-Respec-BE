package kakaotech.bootcamp.respec.specranking.domain.spec.controller;

import jakarta.validation.Valid;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.request.PostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.spec.service.SpecService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
public class SpecController {

    private final SpecService specService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleResponseDto createSpec(
            @RequestPart("spec") @Valid PostSpecRequest request,
            @RequestPart(value = "portfolioFile", required = false) MultipartFile portfolioFile) {

        specService.createSpec(request, portfolioFile);
        return new SimpleResponseDto(true, "스펙 입력 성공");
    }

    @PutMapping("/{specId}")
    public SimpleResponseDto updateSpec(
            @PathVariable Long specId,
            @RequestPart("spec") @Valid PostSpecRequest request,
            @RequestPart(value = "portfolioFile", required = false) MultipartFile portfolioFile) {

        specService.updateSpec(specId, request, portfolioFile);
        return new SimpleResponseDto(true, "스펙 수정 성공");
    }

}