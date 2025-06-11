package kakaotech.bootcamp.respec.specranking.domain.comment.controller;

import jakarta.validation.Valid;
import kakaotech.bootcamp.respec.specranking.domain.comment.dto.*;
import kakaotech.bootcamp.respec.specranking.domain.comment.service.CommentQueryService;
import kakaotech.bootcamp.respec.specranking.domain.comment.service.CommentService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/specs/{specId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentQueryService commentQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentPostResponse createComment(
            @PathVariable Long specId,
            @RequestBody @Valid CommentRequest request) {
        return commentService.createComment(specId, request);
    }

    @PostMapping("/{commentId}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    public ReplyPostResponse createReply(
            @PathVariable Long specId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest request) {
        return commentService.createReply(specId, commentId, request);
    }

    @PatchMapping("/{commentId}")
    public CommentUpdateResponse updateComment(
            @PathVariable Long specId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest request) {
        return commentService.updateComment(specId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    public SimpleResponseDto deleteComment(
            @PathVariable Long specId,
            @PathVariable Long commentId) {
        return commentService.deleteComment(specId, commentId);
    }

    @GetMapping
    public CommentListResponse getComments(
            @PathVariable Long specId,
            @PageableDefault(size = 5) Pageable pageable) {
        return commentQueryService.getComments(specId, pageable);
    }
}
