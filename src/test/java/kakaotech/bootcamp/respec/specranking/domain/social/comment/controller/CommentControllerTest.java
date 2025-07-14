package kakaotech.bootcamp.respec.specranking.domain.social.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentRequest;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentUpdateResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.ReplyPostResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.service.CommentService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"server.private-address=localhost"})
@ActiveProfiles("test")
@DisplayName("Comment Controller 테스트")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Nested
    @DisplayName("공통: PathVariable 유효성 검증 테스트")
    class PathVariableValidationTest {

        @Test
        @DisplayName("실패: 음수 스펙 ID")
        void request_WithNegativeSpecId_ShouldReturnBadRequest() throws Exception {
            // given
            Long invalidSpecId = -1L;
            CommentRequest request = new CommentRequest(DEFAULT_COMMENT_CONTENT);

            // when & then
            mockMvc.perform(post(COMMENT_API_URL, invalidSpecId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패: 문자열 스펙 ID")
        void request_WithStringTypeSpecId_ShouldReturnBadRequest() throws Exception {
            // given
            String invalidSpecId = "invalid";
            CommentRequest request = new CommentRequest(DEFAULT_COMMENT_CONTENT);

            // when & then
            mockMvc.perform(post(COMMENT_API_URL, invalidSpecId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패: 음수 댓글 ID")
        void request_WithNegativeCommentId_ShouldReturnBadRequest() throws Exception {
            // given
            Long invalidCommentId = -2L;
            CommentRequest request = new CommentRequest(DEFAULT_COMMENT_CONTENT);

            // when & then
            mockMvc.perform(patch(COMMENT_API_URL + "/{commentId}", DEFAULT_SPEC_ID, invalidCommentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패: 문자열 댓글 ID")
        void request_WithStringTypeCommentId_ShouldReturnBadRequest() throws Exception {
            // given
            String invalidCommentId = "invalid";
            CommentRequest request = new CommentRequest(DEFAULT_COMMENT_CONTENT);

            // when & then
            mockMvc.perform(patch(COMMENT_API_URL + "/{commentId}", DEFAULT_SPEC_ID, invalidCommentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("댓글 생성 테스트")
    class CommentCreateTest {

        @Test
        @DisplayName("성공: 유효한 요청으로 댓글 생성")
        void createComment_WithValidRequest_ShouldReturnCreated() throws Exception {
            // given
            CommentRequest request = new CommentRequest(DEFAULT_COMMENT_CONTENT);

            CommentPostResponse.CommentData commentData = new CommentPostResponse.CommentData(
                    DEFAULT_COMMENT_ID, DEFAULT_USER_NICKNAME, DEFAULT_USER_PROFILE_URL,
                    request.content(), ROOT_COMMENT_DEPTH, null, 0);
            CommentPostResponse response = CommentPostResponse.success(CommentMessages.COMMENT_CREATE_SUCCESS, commentData);

            given(commentService.createComment(eq(DEFAULT_SPEC_ID), any(CommentRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post(COMMENT_API_URL, DEFAULT_SPEC_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.message").value(CommentMessages.COMMENT_CREATE_SUCCESS))
                    .andExpect(jsonPath("$.data.commentId").value(DEFAULT_COMMENT_ID))
                    .andExpect(jsonPath("$.data.nickname").value(DEFAULT_USER_NICKNAME))
                    .andExpect(jsonPath("$.data.content").value(DEFAULT_COMMENT_CONTENT));

            verify(commentService).createComment(eq(DEFAULT_SPEC_ID), any(CommentRequest.class));
        }

        @Test
        @DisplayName("실패: 빈 내용으로 댓글 생성")
        void createComment_WithEmptyContent_ShouldReturnBadRequest() throws Exception {
            // given
            CommentRequest request = new CommentRequest("");

            // when & then
            mockMvc.perform(post(COMMENT_API_URL, DEFAULT_SPEC_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패: 255자 초과한 내용으로 댓글 생성")
        void createComment_WithTooLongContent_ShouldReturnBadRequest() throws Exception {
            // given
            String longContent = "a".repeat(256);
            CommentRequest request = new CommentRequest(longContent);

            // when & then
            mockMvc.perform(post(COMMENT_API_URL, DEFAULT_SPEC_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패: null 내용으로 댓글 생성")
        void createComment_WithNullContent_ShouldReturnBadRequest() throws Exception {
            // given
            CommentRequest request = new CommentRequest(null);

            // when & then
            mockMvc.perform(post(COMMENT_API_URL, DEFAULT_SPEC_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패: 요청 본문 없이 댓글 생성")
        void createComment_WithoutRequestBody_ShouldReturnBadRequest() throws Exception {
            // when & then
            mockMvc.perform(post(COMMENT_API_URL, DEFAULT_SPEC_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("대댓글 생성 테스트")
    class ReplyCreateTest {

        @Test
        @DisplayName("성공: 유효한 요청으로 대댓글 생성")
        void createReply_WithValidRequest_ShouldReturnCreated() throws Exception {
            // given
            CommentRequest request = new CommentRequest(REPLY_CONTENT);

            ReplyPostResponse.ReplyData replyData = new ReplyPostResponse.ReplyData(
                    REPLY_ID, DEFAULT_USER_NICKNAME, DEFAULT_USER_PROFILE_URL,
                    request.content(), REPLY_DEPTH, DEFAULT_PARENT_COMMENT_ID);
            ReplyPostResponse response = ReplyPostResponse.success(CommentMessages.REPLY_CREATE_SUCCESS, replyData);

            given(commentService.createReply(eq(DEFAULT_SPEC_ID), eq(DEFAULT_PARENT_COMMENT_ID), any(CommentRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post(COMMENT_API_URL + "/{commentId}/replies", DEFAULT_SPEC_ID, DEFAULT_PARENT_COMMENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.message").value(CommentMessages.REPLY_CREATE_SUCCESS))
                    .andExpect(jsonPath("$.data.commentId").value(REPLY_ID))
                    .andExpect(jsonPath("$.data.nickname").value(DEFAULT_USER_NICKNAME))
                    .andExpect(jsonPath("$.data.content").value(REPLY_CONTENT));

            verify(commentService).createReply(eq(DEFAULT_SPEC_ID), eq(DEFAULT_PARENT_COMMENT_ID), any(CommentRequest.class));
        }

        @Test
        @DisplayName("실패: 빈 내용으로 대댓글 생성")
        void createReply_WithEmptyContent_ShouldReturnBadRequest() throws Exception {
            // given
            CommentRequest request = new CommentRequest("");

            // when & then
            mockMvc.perform(post(COMMENT_API_URL + "/{commentId}/replies", DEFAULT_SPEC_ID, DEFAULT_PARENT_COMMENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class CommentUpdateTest {

        @Test
        @DisplayName("성공: 유효한 요청으로 댓글 수정")
        void updateComment_WithValidRequest_ShouldReturnOk() throws Exception {
            // given
            CommentRequest request = new CommentRequest(UPDATED_COMMENT_CONTENT);

            CommentUpdateResponse.CommentUpdateData updateData = new CommentUpdateResponse.CommentUpdateData(
                    DEFAULT_COMMENT_ID, request.content(), DEFAULT_UPDATED_AT);
            CommentUpdateResponse response = CommentUpdateResponse.success(CommentMessages.COMMENT_UPDATE_SUCCESS, updateData);

            given(commentService.updateComment(eq(DEFAULT_SPEC_ID), eq(DEFAULT_COMMENT_ID), any(CommentRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(patch(COMMENT_API_URL + "/{commentId}", DEFAULT_SPEC_ID, DEFAULT_COMMENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.message").value(CommentMessages.COMMENT_UPDATE_SUCCESS))
                    .andExpect(jsonPath("$.data.commentId").value(DEFAULT_COMMENT_ID))
                    .andExpect(jsonPath("$.data.content").value(UPDATED_COMMENT_CONTENT));

            verify(commentService).updateComment(eq(DEFAULT_SPEC_ID), eq(DEFAULT_COMMENT_ID), any(CommentRequest.class));
        }

        @Test
        @DisplayName("실패: 빈 내용으로 댓글 수정")
        void updateComment_WithEmptyContent_ShouldReturnBadRequest() throws Exception {
            // given
            CommentRequest request = new CommentRequest("");

            // when & then
            mockMvc.perform(patch(COMMENT_API_URL + "/{commentId}", DEFAULT_SPEC_ID, DEFAULT_COMMENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class CommentDeleteTest {

        @Test
        @DisplayName("성공: 유효한 요청으로 댓글 삭제")
        void deleteComment_WithValidRequest_ShouldReturnOk() throws Exception {
            // given
            SimpleResponseDto response = SimpleResponseDto.success(CommentMessages.COMMENT_DELETE_SUCCESS);

            given(commentService.deleteComment(eq(DEFAULT_SPEC_ID), eq(DEFAULT_COMMENT_ID)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(delete(COMMENT_API_URL + "/{commentId}", DEFAULT_SPEC_ID, DEFAULT_COMMENT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.message").value(CommentMessages.COMMENT_DELETE_SUCCESS));

            verify(commentService).deleteComment(eq(DEFAULT_SPEC_ID), eq(DEFAULT_COMMENT_ID));
        }
    }
}