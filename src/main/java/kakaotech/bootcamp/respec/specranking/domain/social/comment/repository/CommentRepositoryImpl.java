package kakaotech.bootcamp.respec.specranking.domain.social.comment.repository;

import static kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.QComment.comment;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentListResponse;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.CommentQueryDto;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.dto.ReplyQueryDto;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.QComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private static final int ROOT_COMMENT_DEPTH = 0;
    private static final int REPLY_DEPTH = 1;

    private final JPAQueryFactory queryFactory;
    private final QComment replyComment = new QComment("replyComment");

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<CommentListResponse.CommentWithReplies> findCommentsWithReplies(Long specId, Pageable pageable) {

        List<CommentQueryDto> visibleRootComments = findVisibleRootComments(specId, pageable);

        if (visibleRootComments.isEmpty()) {
            return createEmptyPage(pageable);
        }

        MappedCommentReplyData mappedCommentReplyData = mapCommentAndReplyData(specId, visibleRootComments);
        List<CommentListResponse.CommentWithReplies> resultCommentListData = buildCommentWithReplies(visibleRootComments, mappedCommentReplyData);

        Long totalVisibleRootComments = countTotalVisibleRootComments(specId);
        Long totalActiveCommentsAndReplies = mappedCommentReplyData.totalActiveElements();

        return new CustomPageImpl<>(resultCommentListData, pageable, totalVisibleRootComments, totalActiveCommentsAndReplies);
    }

    //===================
    // Core Query Methods
    //===================

    private List<CommentQueryDto> findVisibleRootComments(Long specId, Pageable pageable) {
        return queryFactory
                .select(createCommentProjection())
                .from(comment)
                .where(isRootCommentInSpec(specId)
                        .and(isVisibleRootComment(specId)))
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private List<ReplyQueryDto> findActiveRepliesByBundles(Long specId, List<Integer> bundles) {
        return queryFactory
                .select(createReplyProjection())
                .from(comment)
                .where(isActiveReplyInBundles(specId, bundles))
                .orderBy(comment.createdAt.asc())
                .fetch();
    }

    //====================
    // Count Query Methods
    //====================

    private Map<Integer, Long> countActiveRepliesByBundles(Long specId, List<Integer> bundles) {
        return queryFactory
                .select(comment.bundle, comment.count())
                .from(comment)
                .where(isActiveReplyInBundles(specId, bundles))
                .groupBy(comment.bundle)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.bundle),
                        tuple -> tuple.get(comment.count())
                ));
    }

    private Long countTotalVisibleRootComments(Long specId) {
        return queryFactory
                .select(comment.count())
                .from(comment)
                .where(isRootCommentInSpec(specId)
                        .and(isVisibleRootComment(specId)))
                .fetchOne();
    }

    private Long countTotalActiveCommentsAndReplies(Long specId) {
        return queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.spec.id.eq(specId)
                        .and(comment.deletedAt.isNull()))
                .fetchOne();
    }

    //===================================
    // QueryDSL BooleanExpression Methods
    //===================================

    private BooleanExpression isRootCommentInSpec(Long specId) {
        return comment.spec.id.eq(specId)
                .and(comment.depth.eq(ROOT_COMMENT_DEPTH));
    }

    private BooleanExpression isVisibleRootComment(Long specId) {
        return isNotDeletedComment()
                .or(isDeletedButHasActiveReplies(specId));
    }

    private BooleanExpression isNotDeletedComment() {
        return comment.deletedAt.isNull();
    }

    private BooleanExpression isDeletedButHasActiveReplies(Long specId) {
        return comment.deletedAt.isNotNull()
                .and(hasActiveRepliesInSameBundle(specId));
    }

    private BooleanExpression hasActiveRepliesInSameBundle(Long specId) {
        return JPAExpressions
                .select(replyComment.count())
                .from(replyComment)
                .where(isActiveReplyInSameBundle(specId))
                .gt(0L);
    }

    private BooleanExpression isActiveReplyInSameBundle(Long specId) {
        return replyComment.spec.id.eq(specId)
                .and(replyComment.bundle.eq(comment.bundle))
                .and(replyComment.depth.eq(REPLY_DEPTH))
                .and(replyComment.deletedAt.isNull());
    }

    private BooleanExpression isActiveReplyInBundles(Long specId, List<Integer> bundles) {
        return comment.spec.id.eq(specId)
                .and(comment.bundle.in(bundles))
                .and(comment.depth.eq(REPLY_DEPTH))
                .and(comment.deletedAt.isNull());
    }

    //========================
    // Data Processing Methods
    //========================

    private MappedCommentReplyData mapCommentAndReplyData(Long specId, List<CommentQueryDto> rootComments) {
        List<Integer> bundles = extractBundleNumbers(rootComments);
        List<ReplyQueryDto> activeReplies = findActiveRepliesByBundles(specId, bundles);
        Map<Integer, Long> replyCountsByBundle = countActiveRepliesByBundles(specId, bundles);
        Map<Integer, List<ReplyQueryDto>> repliesGroupedByBundle = groupRepliesByBundle(activeReplies);
        Long totalActiveElements = countTotalActiveCommentsAndReplies(specId);

        return new MappedCommentReplyData(repliesGroupedByBundle, replyCountsByBundle, totalActiveElements);
    }

    private List<Integer> extractBundleNumbers(List<CommentQueryDto> rootComments) {
        return rootComments.stream().map(CommentQueryDto::bundle).toList();
    }

    private Map<Integer, List<ReplyQueryDto>> groupRepliesByBundle(List<ReplyQueryDto> replies) {
        return replies.stream().collect(Collectors.groupingBy(ReplyQueryDto::bundle));
    }

    //==========================
    // Response Building Methods
    //==========================

    private List<CommentListResponse.CommentWithReplies> buildCommentWithReplies(
            List<CommentQueryDto> rootComments,
            MappedCommentReplyData mappedCommentReplyData) {
        return rootComments.stream()
                .map(rootComment -> convertToCommentWithReplies(rootComment, mappedCommentReplyData))
                .toList();
    }

    private CommentListResponse.CommentWithReplies convertToCommentWithReplies(
            CommentQueryDto rootComment,
            MappedCommentReplyData mappedCommentReplyData) {

        List<ReplyQueryDto> bundleReplies = mappedCommentReplyData.repliesByBundle()
                .getOrDefault(rootComment.bundle(), List.of());

        List<CommentListResponse.ReplyInfo> replyInfos = bundleReplies.stream()
                .map(this::convertToReplyInfo)
                .toList();

        Integer replyCount = mappedCommentReplyData.replyCountsByBundle()
                .getOrDefault(rootComment.bundle(), 0L)
                .intValue();

        return new CommentListResponse.CommentWithReplies(
                rootComment.commentId(),
                rootComment.writerId(),
                rootComment.getMaskedContent(),
                rootComment.getMaskedUserNickname(),
                rootComment.getMaskedProfileImageUrl(),
                rootComment.getFormattedCreatedAt(),
                rootComment.getFormattedUpdatedAt(),
                replyCount,
                replyInfos
        );
    }

    private CommentListResponse.ReplyInfo convertToReplyInfo(ReplyQueryDto replyQueryDto) {
        return new CommentListResponse.ReplyInfo(
                replyQueryDto.replyId(),
                replyQueryDto.writerId(),
                replyQueryDto.content(),
                replyQueryDto.nickname(),
                replyQueryDto.profileImageUrl(),
                replyQueryDto.getFormattedCreatedAt(),
                replyQueryDto.getFormattedUpdatedAt()
        );
    }

    //=============================
    // Projection & Utility Methods
    //=============================

    private ConstructorExpression<CommentQueryDto> createCommentProjection() {
        return Projections.constructor(CommentQueryDto.class,
                comment.id,
                comment.writer.id,
                comment.content,
                comment.writer.nickname,
                comment.writer.userProfileUrl,
                comment.createdAt,
                comment.updatedAt,
                comment.deletedAt,
                comment.bundle);
    }

    private ConstructorExpression<ReplyQueryDto> createReplyProjection() {
        return Projections.constructor(ReplyQueryDto.class,
                comment.id,
                comment.writer.id,
                comment.content,
                comment.writer.nickname,
                comment.writer.userProfileUrl,
                comment.createdAt,
                comment.updatedAt,
                comment.bundle);
    }

    private Page<CommentListResponse.CommentWithReplies> createEmptyPage(Pageable pageable) {
        return new PageImpl<>(List.of(), pageable, 0);
    }

    //==============
    // Inner Records
    //==============

    private record MappedCommentReplyData(
            Map<Integer, List<ReplyQueryDto>> repliesByBundle,
            Map<Integer, Long> replyCountsByBundle,
            Long totalActiveElements
    ) { }
}
