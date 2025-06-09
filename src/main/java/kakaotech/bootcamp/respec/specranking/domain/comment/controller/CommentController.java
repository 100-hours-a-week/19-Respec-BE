package kakaotech.bootcamp.respec.specranking.domain.comment.controller;

import jakarta.validation.Valid;
import kakaotech.bootcamp.respec.specranking.domain.comment.dto.CommentPostRequest;
import kakaotech.bootcamp.respec.specranking.domain.comment.dto.CommentPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.comment.dto.ReplyPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/specs/{specId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentPostResponse createComment(
            @PathVariable Long specId,
            @RequestBody @Valid CommentPostRequest request) {
        return commentService.createComment(specId, request);
    }

    @PostMapping("/{commentId}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    public ReplyPostResponse createReply(
            @PathVariable Long specId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentPostRequest request) {
        return commentService.createReply(specId, commentId, request);
    }
}
