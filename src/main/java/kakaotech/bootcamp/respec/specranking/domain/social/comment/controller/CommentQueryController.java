package kakaotech.bootcamp.respec.specranking.domain.social.comment.controller;

import jakarta.validation.constraints.Positive;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.service.CommentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/specs/{specId}/comments")
@RequiredArgsConstructor
public class CommentQueryController {

    private final CommentQueryService commentQueryService;

    @GetMapping
    public CommentListResponse getComments(
            @PathVariable @Positive(message = "스펙 ID는 양수여야 합니다.") Long specId,
            @PageableDefault(size = 5) Pageable pageable) {
        return commentQueryService.getComments(specId, pageable);
    }
}
