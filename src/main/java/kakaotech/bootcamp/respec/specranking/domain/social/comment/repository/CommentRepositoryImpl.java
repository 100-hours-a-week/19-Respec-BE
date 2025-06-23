package kakaotech.bootcamp.respec.specranking.domain.social.comment.repository;

import static kakaotech.bootcamp.respec.specranking.domain.social.comment.entity.QComment.comment;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    QComment subComment = new QComment("subComment");

    @Override
    public Page<CommentListResponse.CommentWithReplies> findCommentsWithReplies(Long specId, Pageable pageable) {
        List<CommentQueryDto> parentComments = findParentComments(specId, pageable);
        if (parentComments.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<Integer> bundles = extractBundles(parentComments);
        List<ReplyQueryDto> replies = findRepliesBySpecIdAndBundles(specId, bundles);
        Map<Integer, Long> replyCountMap = countRepliesBySpecIdAndBundles(specId, bundles);
        Map<Integer, List<ReplyQueryDto>> repliesByBundle = groupRepliesByBundle(replies);

        List<CommentListResponse.CommentWithReplies> result = buildCommentWithReplies(
                parentComments, repliesByBundle, replyCountMap
        );

        Long totalElements = countTotalCommentsAndReplies(specId);
        Long totalParentComments = countTotalParentComments(specId);

        return new CustomPageImpl<>(result, pageable, totalParentComments, totalElements);
    }

    private List<CommentQueryDto> findParentComments(Long specId, Pageable pageable) {
        return queryFactory
                .select(Projections.constructor(CommentQueryDto.class,
                        comment.id,
                        comment.writer.id,
                        comment.content,
                        comment.writer.nickname,
                        comment.writer.userProfileUrl,
                        comment.createdAt,
                        comment.updatedAt,
                        comment.deletedAt,
                        comment.bundle))
                .from(comment)
                .where(comment.spec.id.eq(specId)
                        .and(comment.depth.eq(0))
                        .and(comment.deletedAt.isNull()
                                .or(comment.deletedAt.isNotNull()
                                        .and(JPAExpressions
                                                .select(subComment.count())
                                                .from(subComment)
                                                .where(subComment.spec.id.eq(specId)
                                                        .and(subComment.bundle.eq(comment.bundle))
                                                        .and(subComment.depth.eq(1))
                                                        .and(subComment.deletedAt.isNull()))
                                                .gt(0L))

                                )))
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private List<Integer> extractBundles(List<CommentQueryDto> parentComments) {
        return parentComments.stream().map(CommentQueryDto::getBundle).toList();
    }

    private List<ReplyQueryDto> findRepliesBySpecIdAndBundles(Long specId, List<Integer> bundles) {
        return queryFactory
                .select(Projections.constructor(ReplyQueryDto.class,
                        comment.id,
                        comment.writer.id,
                        comment.content,
                        comment.writer.nickname,
                        comment.writer.userProfileUrl,
                        comment.createdAt,
                        comment.updatedAt,
                        comment.bundle))
                .from(comment)
                .where(comment.spec.id.eq(specId)
                        .and(comment.bundle.in(bundles))
                        .and(comment.depth.eq(1))
                        .and(comment.deletedAt.isNull()))
                .orderBy(comment.createdAt.asc())
                .fetch();
    }

    private Map<Integer, Long> countRepliesBySpecIdAndBundles(Long specId, List<Integer> bundles) {
        return queryFactory
                .select(comment.bundle, comment.count())
                .from(comment)
                .where(comment.spec.id.eq(specId)
                        .and(comment.bundle.in(bundles))
                        .and(comment.depth.eq(1))
                        .and(comment.deletedAt.isNull()))
                .groupBy(comment.bundle)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.bundle),
                        tuple -> {
                            Long count = tuple.get(comment.count());
                            return count != null ? count : 0L;
                        }
                ));
    }

    private Map<Integer, List<ReplyQueryDto>> groupRepliesByBundle(List<ReplyQueryDto> replies) {
        return replies.stream().collect(Collectors.groupingBy(ReplyQueryDto::getBundle));
    }

    private List<CommentListResponse.CommentWithReplies> buildCommentWithReplies(
            List<CommentQueryDto> parentComments,
            Map<Integer, List<ReplyQueryDto>> repliesByBundle,
            Map<Integer, Long> replyCountMap) {
        return parentComments.stream()
                .map(parentComment -> {
                    List<ReplyQueryDto> childReplies = repliesByBundle.getOrDefault(parentComment.getBundle(),
                            List.of());
                    List<CommentListResponse.ReplyInfo> replyInfos = childReplies.stream().map(this::convertToReplyInfo)
                            .toList();

                    Integer replyCount = replyCountMap.getOrDefault(parentComment.getBundle(), 0L).intValue();

                    String maskedContent = parentComment.isDeleted() ? "삭제된 댓글입니다." : parentComment.getContent();
                    String maskedNickname = parentComment.isDeleted() ? "삭제된 사용자" : parentComment.getNickname();
                    String maskedProfileUrl = parentComment.isDeleted() ? null : parentComment.getProfileImageUrl();

                    return new CommentListResponse.CommentWithReplies(
                            parentComment.getCommentId(),
                            parentComment.getWriterId(),
                            maskedContent,
                            maskedNickname,
                            maskedProfileUrl,
                            formatDateTime(parentComment.getCreatedAt()),
                            formatDateTime(parentComment.getUpdatedAt()),
                            replyCount,
                            replyInfos
                    );
                })
                .toList();
    }

    private CommentListResponse.ReplyInfo convertToReplyInfo(ReplyQueryDto dto) {
        return new CommentListResponse.ReplyInfo(
                dto.getReplyId(),
                dto.getWriterId(),
                dto.getContent(),
                dto.getNickname(),
                dto.getProfileImageUrl(),
                formatDateTime(dto.getCreatedAt()),
                formatDateTime(dto.getUpdatedAt())
        );
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }

    private Long countTotalCommentsAndReplies(Long specId) {
        return queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.spec.id.eq(specId)
                        .and(comment.deletedAt.isNull()))
                .fetchOne();
    }

    private Long countTotalParentComments(Long specId) {
        return queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.spec.id.eq(specId)
                        .and(comment.depth.eq(0))
                        .and(comment.deletedAt.isNull()
                                .or(comment.deletedAt.isNotNull()
                                        .and(JPAExpressions
                                                .select(subComment.count())
                                                .from(subComment)
                                                .where(subComment.spec.id.eq(specId)
                                                        .and(subComment.bundle.eq(comment.bundle))
                                                        .and(subComment.depth.eq(1))
                                                        .and(subComment.deletedAt.isNull()))
                                                .gt(0L))
                                )))
                .fetchOne();
    }
}
