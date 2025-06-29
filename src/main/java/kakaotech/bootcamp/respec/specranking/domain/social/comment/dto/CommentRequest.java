package kakaotech.bootcamp.respec.specranking.domain.social.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest (
    @NotBlank(message = "댓글이나 대댓글 내용은 필수입니다.")
    @Size(max = 255, message = "댓글은 255자를 초과할 수 없습니다.")
    String content
) { }
