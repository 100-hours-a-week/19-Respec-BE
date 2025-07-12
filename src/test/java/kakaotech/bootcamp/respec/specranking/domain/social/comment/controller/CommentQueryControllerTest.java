package kakaotech.bootcamp.respec.specranking.domain.social.comment.controller;

import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.service.CommentQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"server.private-address=localhost"})
@ActiveProfiles("test")
@DisplayName("CommentQuery Controller 테스트")
class CommentQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentQueryService commentQueryService;

    private static final String COMMENT_API_URL = "/api/specs/{specId}/comments";

    @Nested
    @DisplayName("PathVariable 유효성 검증 테스트")
    class PathVariableValidationTest {

        @Test
        @DisplayName("실패: 음수 스펙 ID로 댓글 목록 조회")
        void getComments_WithNegativeSpecId_ShouldReturnBadRequest() throws Exception {
            // given
            Long invalidSpecId = -1L;

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, invalidSpecId))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패: 문자열 스펙 ID로 댓글 목록 조회")
        void getComments_WithStringSpecId_ShouldReturnBadRequest() throws Exception {
            // given
            String invalidSpecId = "invalid";

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, invalidSpecId))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("댓글 목록 조회 테스트")
    class GetCommentsTest {

        @Test
        @DisplayName("성공: 유효한 스펙 ID로 댓글 목록 조회")
        void getComments_WithValidSpecId_ShouldReturnOk() throws Exception {
            // given
            CommentListResponse.ReplyInfo replyInfo = new CommentListResponse.ReplyInfo(
                    REPLY_ID, DEFAULT_USER_ID, REPLY_CONTENT, DEFAULT_USER_NICKNAME,
                    DEFAULT_USER_PROFILE_URL, DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT);

            CommentListResponse.CommentWithReplies commentWithReplies = new CommentListResponse.CommentWithReplies(
                    DEFAULT_COMMENT_ID, DEFAULT_USER_ID, DEFAULT_COMMENT_CONTENT, DEFAULT_USER_NICKNAME,
                    DEFAULT_USER_PROFILE_URL, DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT, 1, List.of(replyInfo));

            CommentListResponse.PageInfo pageInfo = new CommentListResponse.PageInfo(
                    0, 5, 1L, 1, true, true);

            CommentListResponse.CommentListData commentListData = new CommentListResponse.CommentListData(
                    List.of(commentWithReplies), pageInfo);

            CommentListResponse response = CommentListResponse.success(CommentMessages.GET_COMMENT_LIST_SUCCESS, commentListData);

            given(commentQueryService.getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, DEFAULT_SPEC_ID)
                            .param("page", "0")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.message").value(CommentMessages.GET_COMMENT_LIST_SUCCESS))
                    .andExpect(jsonPath("$.data.comments").isArray())
                    .andExpect(jsonPath("$.data.comments[0].commentId").value(DEFAULT_COMMENT_ID))
                    .andExpect(jsonPath("$.data.comments[0].content").value(DEFAULT_COMMENT_CONTENT))
                    .andExpect(jsonPath("$.data.comments[0].writerId").value(DEFAULT_USER_ID))
                    .andExpect(jsonPath("$.data.comments[0].nickname").value(DEFAULT_USER_NICKNAME))
                    .andExpect(jsonPath("$.data.comments[0].replyCount").value(1))
                    .andExpect(jsonPath("$.data.comments[0].replies").isArray())
                    .andExpect(jsonPath("$.data.comments[0].replies[0].replyId").value(REPLY_ID))
                    .andExpect(jsonPath("$.data.comments[0].replies[0].content").value(REPLY_CONTENT))
                    .andExpect(jsonPath("$.data.pageInfo.pageNumber").value(0))
                    .andExpect(jsonPath("$.data.pageInfo.pageSize").value(5))
                    .andExpect(jsonPath("$.data.pageInfo.totalElements").value(1L))
                    .andExpect(jsonPath("$.data.pageInfo.totalPages").value(1))
                    .andExpect(jsonPath("$.data.pageInfo.isFirst").value(true))
                    .andExpect(jsonPath("$.data.pageInfo.isLast").value(true));

            verify(commentQueryService).getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class));
        }

        @Test
        @DisplayName("성공: 페이지 파라미터 없이 댓글 목록 조회 (기본값 사용)")
        void getComments_WithoutPageParameters_ShouldUseDefaultValues() throws Exception {
            // given
            CommentListResponse.PageInfo pageInfo = new CommentListResponse.PageInfo(
                    0, 5, 0L, 0, true, true);

            CommentListResponse.CommentListData commentListData = new CommentListResponse.CommentListData(
                    List.of(), pageInfo);

            CommentListResponse response = CommentListResponse.success(CommentMessages.GET_COMMENT_LIST_SUCCESS, commentListData);

            given(commentQueryService.getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, DEFAULT_SPEC_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.data.comments").isArray())
                    .andExpect(jsonPath("$.data.pageInfo.pageSize").value(5));

            verify(commentQueryService).getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class));
        }

        @Test
        @DisplayName("성공: 커스텀 페이지 크기로 댓글 목록 조회")
        void getComments_WithCustomPageSize_ShouldReturnOk() throws Exception {
            // given
            CommentListResponse.PageInfo pageInfo = new CommentListResponse.PageInfo(
                    0, 10, 0L, 0, true, true);

            CommentListResponse.CommentListData commentListData = new CommentListResponse.CommentListData(
                    List.of(), pageInfo);

            CommentListResponse response = CommentListResponse.success(CommentMessages.GET_COMMENT_LIST_SUCCESS, commentListData);

            given(commentQueryService.getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, DEFAULT_SPEC_ID)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.pageInfo.pageSize").value(10));

            verify(commentQueryService).getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class));
        }

        @Test
        @DisplayName("성공: 빈 댓글 목록 조회")
        void getComments_WithEmptyComments_ShouldReturnEmptyList() throws Exception {
            // given
            CommentListResponse.PageInfo pageInfo = new CommentListResponse.PageInfo(
                    0, 5, 0L, 0, true, true);

            CommentListResponse.CommentListData commentListData = new CommentListResponse.CommentListData(
                    List.of(), pageInfo);

            CommentListResponse response = CommentListResponse.success(CommentMessages.GET_COMMENT_LIST_SUCCESS, commentListData);

            given(commentQueryService.getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, DEFAULT_SPEC_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.data.comments").isArray())
                    .andExpect(jsonPath("$.data.comments").isEmpty())
                    .andExpect(jsonPath("$.data.pageInfo.totalElements").value(0L));

            verify(commentQueryService).getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class));
        }

        @Test
        @DisplayName("성공: 대댓글이 있는 댓글 목록 조회")
        void getComments_WithReplies_ShouldReturnCommentsWithReplies() throws Exception {
            // given
            CommentListResponse.ReplyInfo reply1 = new CommentListResponse.ReplyInfo(
                    REPLY_ID, DEFAULT_USER_ID, "첫 번째 대댓글", DEFAULT_USER_NICKNAME,
                    DEFAULT_USER_PROFILE_URL, DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT);

            CommentListResponse.ReplyInfo reply2 = new CommentListResponse.ReplyInfo(
                    REPLY_ID + 1L, DEFAULT_USER_ID, "두 번째 대댓글", DEFAULT_USER_NICKNAME,
                    DEFAULT_USER_PROFILE_URL, DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT);

            CommentListResponse.CommentWithReplies commentWithReplies = new CommentListResponse.CommentWithReplies(
                    DEFAULT_COMMENT_ID, DEFAULT_USER_ID, DEFAULT_COMMENT_CONTENT, DEFAULT_USER_NICKNAME,
                    DEFAULT_USER_PROFILE_URL, DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT, 2, List.of(reply1, reply2));

            CommentListResponse.PageInfo pageInfo = new CommentListResponse.PageInfo(
                    0, 5, 1L, 1, true, true);

            CommentListResponse.CommentListData commentListData = new CommentListResponse.CommentListData(
                    List.of(commentWithReplies), pageInfo);

            CommentListResponse response = CommentListResponse.success(CommentMessages.GET_COMMENT_LIST_SUCCESS, commentListData);

            given(commentQueryService.getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, DEFAULT_SPEC_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.comments[0].replyCount").value(2))
                    .andExpect(jsonPath("$.data.comments[0].replies").isArray())
                    .andExpect(jsonPath("$.data.comments[0].replies[0].replyId").value(REPLY_ID))
                    .andExpect(jsonPath("$.data.comments[0].replies[0].content").value("첫 번째 대댓글"))
                    .andExpect(jsonPath("$.data.comments[0].replies[1].replyId").value(REPLY_ID + 1L))
                    .andExpect(jsonPath("$.data.comments[0].replies[1].content").value("두 번째 대댓글"));

            verify(commentQueryService).getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class));
        }

        @Test
        @DisplayName("성공: 페이지네이션 정보 확인")
        void getComments_ShouldReturnPaginationInfo() throws Exception {
            // given
            CommentListResponse.PageInfo pageInfo = new CommentListResponse.PageInfo(
                    1, 5, 15L, 3, false, false);

            CommentListResponse.CommentListData commentListData = new CommentListResponse.CommentListData(
                    List.of(), pageInfo);

            CommentListResponse response = CommentListResponse.success(CommentMessages.GET_COMMENT_LIST_SUCCESS, commentListData);

            given(commentQueryService.getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, DEFAULT_SPEC_ID)
                            .param("page", "1")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.pageInfo.totalPages").value(3))
                    .andExpect(jsonPath("$.data.pageInfo.pageNumber").value(1))
                    .andExpect(jsonPath("$.data.pageInfo.pageSize").value(5))
                    .andExpect(jsonPath("$.data.pageInfo.totalElements").value(15L))
                    .andExpect(jsonPath("$.data.pageInfo.isFirst").value(false))
                    .andExpect(jsonPath("$.data.pageInfo.isLast").value(false));

            verify(commentQueryService).getComments(eq(DEFAULT_SPEC_ID), any(Pageable.class));
        }
    }
}