package kakaotech.bootcamp.respec.specranking.domain.spec.spec.controller;

import jakarta.validation.Valid;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.request.PostSpecRequest;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.SpecService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specs")
@RequiredArgsConstructor
public class SpecController {

    private final SpecService specService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleResponseDto createSpec(
            @RequestBody @Valid PostSpecRequest request) {

        specService.createSpec(request);
        return new SimpleResponseDto(true, "스펙 입력 성공");
    }

    @PutMapping("/{specId}")
    public SimpleResponseDto updateSpec(
            @PathVariable Long specId,
            @RequestBody @Valid PostSpecRequest request) {

        specService.updateSpec(specId, request);
        return new SimpleResponseDto(true, "스펙 수정 성공");
    }

}
