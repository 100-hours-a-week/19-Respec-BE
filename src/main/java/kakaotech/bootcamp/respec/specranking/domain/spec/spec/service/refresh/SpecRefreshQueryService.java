package kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.refresh;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedRankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SpecMetaResponse.Meta;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository.SpecRankingProjection;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SpecRefreshQueryService {

    private final UserRepository userRepository;
    private final SpecRepository specRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;

    public Meta getMetaDataFromDb(JobField jobField) {
        long totalUserCount = 0;
        Double averageScore = 0.0;

        if (jobField == JobField.TOTAL) {
            totalUserCount = userRepository.countUsersHavingSpec();
            averageScore = specRepository.findAverageScoreByJobField(null);
        } else {
            totalUserCount = specRepository.countByJobField(jobField);
            averageScore = specRepository.findAverageScoreByJobField(jobField);
        }

        if (averageScore == null) {
            averageScore = 0.0;
        }

        Meta meta = new Meta(totalUserCount, averageScore);

        return meta;
    }

    public CachedRankingResponse getRankingDataFromDb(JobField jobField, int limit) {
        // 1. Spec 리스트 조회
        List<Spec> specs = specRepository.findTopSpecsByJobFieldWithCursor(jobField, Long.MAX_VALUE, limit + 1);

        boolean hasNext = specs.size() > limit;
        if (hasNext) {
            specs = specs.subList(0, limit);
        }

        String nextCursor = null;
        if (hasNext) {
            nextCursor = encodeCursor(specs.getLast().getId());
        }

        // 2. Spec ID 리스트 추출
        List<Long> specIds = specs.stream().map(Spec::getId).toList();

        // 3. 벌크 조회들을 병렬로 실행
        CompletableFuture<Map<Long, Long>> totalRanksFuture = CompletableFuture.supplyAsync(() -> {
            return specRepository.findRankingsBySpecIds(specIds, "TOTAL").stream()
                    .collect(Collectors.toMap(
                            SpecRankingProjection::getSpecId,
                            SpecRankingProjection::getTotalRank
                    ));
        });

        CompletableFuture<Map<Long, Long>> jobFieldRanksFuture = CompletableFuture.supplyAsync(() -> {
            return specRepository.findRankingsBySpecIds(specIds, jobField.name()).stream()
                    .collect(Collectors.toMap(
                            SpecRankingProjection::getSpecId,
                            SpecRankingProjection::getJobFieldRank
                    ));
        });

        CompletableFuture<Map<Long, Long>> commentsCountFuture = CompletableFuture.supplyAsync(() -> {
            return commentRepository.countBySpecIds(specIds).stream()
                    .collect(Collectors.toMap(
                            CommentRepository.SpecCountProjection::getSpecId,
                            CommentRepository.SpecCountProjection::getCount
                    ));
        });

        CompletableFuture<Map<Long, Long>> bookmarksCountFuture = CompletableFuture.supplyAsync(() -> {
            return bookmarkRepository.countBySpecIds(specIds).stream()
                    .collect(Collectors.toMap(
                            BookmarkRepository.SpecCountProjection::getSpecId,
                            BookmarkRepository.SpecCountProjection::getCount
                    ));
        });

        CompletableFuture<Long> totalUserCountFuture = CompletableFuture.supplyAsync(() ->
                userRepository.countUsersHavingSpec()
        );

        CompletableFuture<Long> usersCountByJobFieldFuture = CompletableFuture.supplyAsync(() ->
                specRepository.countByJobField(jobField)
        );

        // 4. 모든 비동기 작업 완료 대기
        CompletableFuture.allOf(
                totalRanksFuture, jobFieldRanksFuture,
                commentsCountFuture, bookmarksCountFuture,
                totalUserCountFuture, usersCountByJobFieldFuture
        ).join();

        // 5. 결과 조합
        Map<Long, Long> totalRanks = totalRanksFuture.join();
        Map<Long, Long> jobFieldRanks = jobFieldRanksFuture.join();
        Map<Long, Long> commentsCounts = commentsCountFuture.join();
        Map<Long, Long> bookmarksCounts = bookmarksCountFuture.join();
        Long totalUserCount = totalUserCountFuture.join();
        Long usersCountByJobField = usersCountByJobFieldFuture.join();

        List<CachedRankingResponse.CachedRankingItem> items = specs.stream().map(spec -> {
            User user = spec.getUser();
            Long specId = spec.getId();

            return new CachedRankingResponse.CachedRankingItem(
                    user.getId(),
                    user.getNickname(),
                    user.getUserProfileUrl(),
                    specId,
                    spec.getTotalAnalysisScore(),
                    totalRanks.getOrDefault(specId, 0L),
                    totalUserCount,
                    spec.getJobField(),
                    jobFieldRanks.getOrDefault(specId, 0L),
                    usersCountByJobField,
                    commentsCounts.getOrDefault(specId, 0L),
                    bookmarksCounts.getOrDefault(specId, 0L)
            );
        }).toList();

        return new CachedRankingResponse(items, hasNext, nextCursor);
    }

    private String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(String.valueOf(id).getBytes());
    }
}
