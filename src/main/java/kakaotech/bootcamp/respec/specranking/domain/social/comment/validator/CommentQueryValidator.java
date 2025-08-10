package kakaotech.bootcamp.respec.specranking.domain.social.comment.validator;

import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentQueryValidator {

    private final SpecRepository specRepository;

    public void validateSpecExists(Long specId) {
        specRepository.findByIdAndStatus(specId, SpecStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SPEC_NOT_FOUND));
    }
}
