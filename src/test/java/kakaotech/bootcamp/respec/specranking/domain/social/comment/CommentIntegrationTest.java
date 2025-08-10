package kakaotech.bootcamp.respec.specranking.domain.social.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.constants.CommentMessages;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentRequest;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.Comment;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.fixture.RepositoryTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"server.private-address=localhost"})
@ActiveProfiles("test")
@Transactional
@DisplayName("Comment 통합 테스트")
class CommentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpecRepository specRepository;

    private User testUser;
    private Spec testSpec;
    private Comment parentComment;

    @BeforeEach
    void setUp() {
        testUser = RepositoryTestFixture.createAndSaveDefaultUserForIntegration(userRepository);
        testSpec = RepositoryTestFixture.createAndSaveDefaultSpecForIntegration(specRepository, testUser);
        parentComment = RepositoryTestFixture.createAndSaveRootCommentForIntegration(
                commentRepository, FIRST_COMMENT_CONTENT, testUser,
                testSpec, INITIAL_BUNDLE_NUMBER
        );
    }

    @Nested
    @DisplayName("댓글 작성 통합 테스트")
    class CommentCreateIntegrationTest {

        @Test
        @DisplayName("성공: 새로운 댓글 생성")
        void createComment_Success() throws Exception {
            // given
            CommentRequest request = new CommentRequest(DEFAULT_COMMENT_CONTENT);

            // when & then
            mockMvc.perform(post(COMMENT_API_URL, testSpec.getId())
                            .with(authentication(createAuthentication(testUser.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.message").value(CommentMessages.COMMENT_CREATE_SUCCESS))
                    .andExpect(jsonPath("$.data.content").value(DEFAULT_COMMENT_CONTENT))
                    .andExpect(jsonPath("$.data.depth").value(ROOT_COMMENT_DEPTH))
                    .andExpect(jsonPath("$.data.parentCommentId").isEmpty());

            // DB check
            List<Comment> comments = commentRepository.findAll();
            assertThat(comments).hasSize(2);

            Comment newComment = comments.getLast();

            assertThat(newComment.getContent()).isEqualTo(DEFAULT_COMMENT_CONTENT);
            assertThat(newComment.getDepth()).isEqualTo(ROOT_COMMENT_DEPTH);
            assertThat(newComment.getParentComment()).isNull();
            assertThat(newComment.getWriter()).isEqualTo(testUser);
            assertThat(newComment.getSpec()).isEqualTo(testSpec);
            assertThat(newComment.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("실패: 존재하지 않는 스펙에 댓글 생성")
        void createComment_WithNonExistentSpec_ShouldFail() throws Exception {
            // given
            CommentRequest request = new CommentRequest(DEFAULT_COMMENT_CONTENT);

            // when & then
            mockMvc.perform(post(COMMENT_API_URL, NON_EXISTENT_SPEC_ID)
                            .with(authentication(createAuthentication(testUser.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("실패: 유효하지 않은 댓글 내용")
        void createComment_WithInvalidContent_ShouldFail() throws Exception {
            // given
            CommentRequest request = new CommentRequest("");

            // when & then
            mockMvc.perform(post(COMMENT_API_URL, testSpec.getId())
                            .with(authentication(createAuthentication(testUser.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("대댓글 작성 통합 테스트")
    class ReplyCreateIntegrationTest {

        @Test
        @DisplayName("성공: 새로운 대댓글 생성")
        void createReply_Success() throws Exception {
            // given
            CommentRequest request = new CommentRequest(REPLY_CONTENT);

            // when & then
            mockMvc.perform(post(COMMENT_API_URL + "/{commentId}/replies", testSpec.getId(), parentComment.getId())
                            .with(authentication(createAuthentication(testUser.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.message").value(CommentMessages.REPLY_CREATE_SUCCESS))
                    .andExpect(jsonPath("$.data.content").value(REPLY_CONTENT))
                    .andExpect(jsonPath("$.data.depth").value(REPLY_DEPTH))
                    .andExpect(jsonPath("$.data.parentCommentId").value(parentComment.getId()));

            // DB check
            List<Comment> comments = commentRepository.findAll();
            assertThat(comments).hasSize(2);

            Comment newReply = comments.getLast();

            assertThat(newReply.getContent()).isEqualTo(REPLY_CONTENT);
            assertThat(newReply.getDepth()).isEqualTo(REPLY_DEPTH);
            assertThat(newReply.getParentComment()).isEqualTo(parentComment);
            assertThat(newReply.getWriter()).isEqualTo(testUser);
            assertThat(newReply.getSpec()).isEqualTo(testSpec);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 부모 댓글에 대댓글 생성")
        void createReply_WithNonExistentParentComment_ShouldFail() throws Exception {
            // given
            CommentRequest request = new CommentRequest(REPLY_CONTENT);

            // when & then
            mockMvc.perform(post(COMMENT_API_URL + "/{commentId}/replies", testSpec.getId(), NON_EXISTENT_PARENT_COMMENT_ID)
                            .with(authentication(createAuthentication(testUser.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("댓글 수정 통합 테스트")
    class CommentUpdateIntegrationTest {

        @Test
        @DisplayName("성공: 댓글 수정")
        void updateComment_Success() throws Exception {
            // given
            CommentRequest request = new CommentRequest(UPDATED_COMMENT_CONTENT);

            // when & then
            mockMvc.perform(patch(COMMENT_API_URL + "/{commentId}", testSpec.getId(), parentComment.getId())
                            .with(authentication(createAuthentication(testUser.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.message").value(CommentMessages.COMMENT_UPDATE_SUCCESS))
                    .andExpect(jsonPath("$.data.content").value(UPDATED_COMMENT_CONTENT));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 댓글 수정")
        void updateComment_WithNonExistentComment_ShouldFail() throws Exception {
            // given
            CommentRequest request = new CommentRequest(UPDATED_COMMENT_CONTENT);

            // when & then
            mockMvc.perform(patch(COMMENT_API_URL + "/{commentId}", testSpec.getId(), NON_EXISTENT_COMMENT_ID)
                            .with(authentication(createAuthentication(testUser.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("댓글 삭제 통합 테스트")
    class CommentDeleteIntegrationTest {

        @Test
        @DisplayName("성공: 대댓글이 있는 댓글 삭제")
        void deleteComment_Success() throws Exception {
            // when & then
            mockMvc.perform(delete(COMMENT_API_URL + "/{commentId}", testSpec.getId(), parentComment.getId())
                            .with(authentication(createAuthentication(testUser.getId()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.message").value(CommentMessages.COMMENT_DELETE_SUCCESS));

            // DB check
            Comment deletedComment = commentRepository.findById(parentComment.getId()).orElseThrow();
            assertThat(deletedComment.isDeleted()).isTrue();
            assertThat(deletedComment.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("실패: 존재하지 않는 댓글 삭제")
        void deleteComment_WithNonExistentComment_ShouldFail() throws Exception {
            // when & then
            mockMvc.perform(delete(COMMENT_API_URL + "/{commentId}", testSpec.getId(), NON_EXISTENT_COMMENT_ID)
                            .with(authentication(createAuthentication(testUser.getId()))))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("댓글 목록 조회 통합 테스트")
    class GetCommentsIntegrationTest {

        @Test
        @DisplayName("성공: 댓글 및 대댓글 목록 조회")
        void getComments_WithCommentsAndReplies_Success() throws Exception {
            // given
            Comment reply1 = RepositoryTestFixture.createAndSaveReplyForIntegration(
                    commentRepository, "첫 번째 대댓글", testUser, testSpec,
                    parentComment, INITIAL_BUNDLE_NUMBER
            );
            Comment reply2 = RepositoryTestFixture.createAndSaveReplyForIntegration(
                    commentRepository, "두 번째 대댓글", testUser, testSpec,
                    parentComment, INITIAL_BUNDLE_NUMBER
            );

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, testSpec.getId())
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.data.comments").isArray())
                    .andExpect(jsonPath("$.data.comments[0].content").value(FIRST_COMMENT_CONTENT))
                    .andExpect(jsonPath("$.data.comments[0].replyCount").value(2))
                    .andExpect(jsonPath("$.data.comments[0].replies").isArray())
                    .andExpect(jsonPath("$.data.comments[0].replies[0].content").value("첫 번째 대댓글"))
                    .andExpect(jsonPath("$.data.comments[0].replies[1].content").value("두 번째 대댓글"))
                    .andExpect(jsonPath("$.data.pageInfo.totalElements").value(3))
                    .andExpect(jsonPath("$.data.pageInfo.pageNumber").value(0))
                    .andExpect(jsonPath("$.data.pageInfo.pageSize").value(10));
        }

        @Test
        @DisplayName("성공: 빈 댓글 목록 조회")
        void getComments_WithEmptyComments_Success() throws Exception {
            // given
            commentRepository.deleteAll();

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, testSpec.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.data.comments").isArray())
                    .andExpect(jsonPath("$.data.comments").isEmpty())
                    .andExpect(jsonPath("$.data.pageInfo.totalElements").value(0));
        }

        @Test
        @DisplayName("성공: 페이지네이션 테스트")
        void getComments_WithPagination_Success() throws Exception {
            // given
            for (int i = 1; i <= 5; i++) {
                RepositoryTestFixture.createAndSaveRootCommentForIntegration(
                        commentRepository, "댓글 " + i, testUser, testSpec, i
                );
            }

            // when & then
            mockMvc.perform(get(COMMENT_API_URL, testSpec.getId())
                            .param("page", "0")
                            .param("size", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.comments").isArray())
                    .andExpect(jsonPath("$.data.comments.length()").value(3))
                    .andExpect(jsonPath("$.data.pageInfo.totalElements").value(6))
                    .andExpect(jsonPath("$.data.pageInfo.totalPages").value(2))
                    .andExpect(jsonPath("$.data.pageInfo.isFirst").value(true))
                    .andExpect(jsonPath("$.data.pageInfo.isLast").value(false));

            mockMvc.perform(get(COMMENT_API_URL, testSpec.getId())
                            .param("page", "1")
                            .param("size", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.comments.length()").value(3))
                    .andExpect(jsonPath("$.data.pageInfo.isFirst").value(false))
                    .andExpect(jsonPath("$.data.pageInfo.isLast").value(true));
        }
    }

    @Nested
    @DisplayName("전체 플로우 통합 테스트")
    class FullFlowIntegrationTest {

        @Test
        @DisplayName("성공: 댓글 생성 -> 대댓글 생성 -> 조회 -> 수정 -> 삭제 플로우")
        void fullCommentFlow_Success() throws Exception {
            // 1. create new comment
            CommentRequest createRequest = new CommentRequest("통합테스트 댓글");

            mockMvc.perform(post(COMMENT_API_URL, testSpec.getId())
                            .with(authentication(createAuthentication(testUser.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated());

            // 2. find new comment
            Comment newComment = commentRepository.findAll().getFirst();

            // 3. create reply
            CommentRequest replyRequest = new CommentRequest("통합테스트 대댓글");

            mockMvc.perform(post(COMMENT_API_URL + "/{commentId}/replies", testSpec.getId(), newComment.getId())
                            .with(authentication(createAuthentication(testUser.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(replyRequest)))
                    .andExpect(status().isCreated());

            // 4. get comment list
            mockMvc.perform(get(COMMENT_API_URL, testSpec.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.comments[*].content").value(org.hamcrest.Matchers.hasItem("통합테스트 댓글")));

            // 5. update comment
            CommentRequest updateRequest = new CommentRequest("수정된 통합테스트 댓글");

            mockMvc.perform(patch(COMMENT_API_URL + "/{commentId}", testSpec.getId(), newComment.getId())
                            .with(authentication(createAuthentication(testUser.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk());

            // 6. delete comment
            mockMvc.perform(delete(COMMENT_API_URL + "/{commentId}", testSpec.getId(), newComment.getId())
                            .with(authentication(createAuthentication(testUser.getId()))))
                    .andExpect(status().isOk());

            Comment finalComment = commentRepository.findById(newComment.getId()).orElseThrow();
            assertThat(finalComment.isDeleted()).isTrue();
            assertThat(finalComment.getDeletedAt()).isNotNull();

            // 7. get comment list (check comment deletion)
            mockMvc.perform(get(COMMENT_API_URL, testSpec.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.comments[*].content")
                            .value(org.hamcrest.Matchers.hasItem("삭제된 댓글입니다.")));
        }
    }

    private Authentication createAuthentication(Long userId) {
        AuthenticatedUserDto principal = new AuthenticatedUserDto(
                userId, DEFAULT_USER_LOGIN_ID, DEFAULT_USER_NICKNAME, DEFAULT_USER_PROFILE_URL);
        return new UsernamePasswordAuthenticationToken(principal, "", List.of());
    }
}
