package kakaotech.bootcamp.respec.specranking.domain.social.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentRequest;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentUpdateResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.ReplyPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.service.CommentQueryService;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.service.CommentService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/specs/{specId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentQueryService commentQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentPostResponse createComment(
            @PathVariable @Positive(message = "스펙 ID는 양수여야 합니다.") Long specId,
            @RequestBody @Valid CommentRequest request) {
        return commentService.createComment(specId, request);
    }

    @PostMapping("/{commentId}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    public ReplyPostResponse createReply(
            @PathVariable @Positive(message = "스펙 ID는 양수여야 합니다.") Long specId,
            @PathVariable @Positive(message = "댓글 ID는 양수여야 합니다.") Long commentId,
            @RequestBody @Valid CommentRequest request) {
        return commentService.createReply(specId, commentId, request);
    }

    @PatchMapping("/{commentId}")
    public CommentUpdateResponse updateComment(
            @PathVariable @Positive(message = "스펙 ID는 양수여야 합니다.") Long specId,
            @PathVariable @Positive(message = "댓글 ID는 양수여야 합니다.") Long commentId,
            @RequestBody @Valid CommentRequest request) {
        return commentService.updateComment(specId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    public SimpleResponseDto deleteComment(
            @PathVariable @Positive(message = "스펙 ID는 양수여야 합니다.") Long specId,
            @PathVariable @Positive(message = "댓글 ID는 양수여야 합니다.") Long commentId) {
        return commentService.deleteComment(specId, commentId);
    }

    @GetMapping
    public CommentListResponse getComments(
            @PathVariable @Positive(message = "스펙 ID는 양수여야 합니다.") Long specId,
            @PageableDefault(size = 5) Pageable pageable) {
        return commentQueryService.getComments(specId, pageable);
    }
}
