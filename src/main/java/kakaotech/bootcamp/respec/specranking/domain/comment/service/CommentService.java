package kakaotech.bootcamp.respec.specranking.domain.comment.service;

import kakaotech.bootcamp.respec.specranking.domain.comment.dto.CommentPostRequest;
import kakaotech.bootcamp.respec.specranking.domain.comment.dto.CommentPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final SpecRepository specRepository;
    private final UserRepository userRepository;

    public CommentPostResponse createComment(Long specId, CommentPostRequest request) {
        Optional<Long> optUserId = UserUtils.getCurrentUserId();
        Long userId = optUserId.orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Spec spec = specRepository.findByIdAndStatus(specId, SpecStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("스펙을 찾을 수 없습니다. ID: " + specId));

        Integer maxBundle = commentRepository.findMaxBundleBySpecId(specId);
        int newBundle = (maxBundle == null) ? 1 : maxBundle + 1;

        Comment comment = new Comment(spec, null, request.getContent(), newBundle, 0);
        Comment savedComment = commentRepository.save(comment);

        CommentPostResponse.CommentData commentData = new CommentPostResponse.CommentData(
                savedComment.getId(),
                user.getNickname(),
                user.getUserProfileUrl(),
                savedComment.getContent(),
                savedComment.getDepth(),
                savedComment.getParentComment() != null ? savedComment.getParentComment().getId() : null,
                0
        );

        return new CommentPostResponse(true, "댓글 작성 성공", commentData);
    }
}
