package kakaotech.bootcamp.respec.specranking.domain.social.comment.validator;

import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.fixture.SpecFixture;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 목록 조회 검증 로직 테스트")
class CommentQueryValidatorTest {

    @Mock
    private SpecRepository specRepository;

    @InjectMocks
    private CommentQueryValidator commentQueryValidator;

    @Test
    @DisplayName("성공: 활성 스펙 존재 검증")
    void validateSpecExists_WhenActiveSpecExists_ShouldNotThrowException() {
        // given
        Spec activeSpec = SpecFixture.createMockSpec();
        given(specRepository.findByIdAndStatus(DEFAULT_SPEC_ID, SpecStatus.ACTIVE))
                .willReturn(Optional.of(activeSpec));

        // when & then
        assertThatCode(() -> commentQueryValidator.validateSpecExists(DEFAULT_SPEC_ID)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("실패: 스펙을 찾을 수 없음")
    void validateSpecExists_WhenSpecNotFound_ShouldThrowException() {
        // given
        given(specRepository.findByIdAndStatus(NON_EXISTENT_SPEC_ID, SpecStatus.ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentQueryValidator.validateSpecExists(NON_EXISTENT_SPEC_ID))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.SPEC_NOT_FOUND.getMessage());
    }
}