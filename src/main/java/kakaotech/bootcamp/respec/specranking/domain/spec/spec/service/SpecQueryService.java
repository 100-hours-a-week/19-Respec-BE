package kakaotech.bootcamp.respec.specranking.domain.spec.spec.service;

import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.QSpec.spec;
import static kakaotech.bootcamp.respec.specranking.global.common.util.CursorUtils.decodeCursor;
import static kakaotech.bootcamp.respec.specranking.global.common.util.CursorUtils.encodeCursor;

import com.querydsl.core.Tuple;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedMetaResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedMetaResponse.CachedMeta;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedRankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.RankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.RankingResponse.RankingData;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.RankingResponse.RankingItem;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SearchResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SearchResponse.SearchData;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SpecMetaResponse.Meta;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.cache.SpecCacheRefreshService;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.refresh.SpecRefreshQueryService;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SpecQueryService {

    private final SpecRepository specRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SpecCacheRefreshService specCacheRefreshService;
    private final SpecRefreshQueryService specRefreshQueryService;

    public RankingData getRankings(JobField jobField, String cursor, int limit) {
        if (cursor == null) {
            String cacheKey = "rankings::" + jobField.name() + "::" + limit;
            CachedRankingResponse cached = (CachedRankingResponse) redisTemplate.opsForValue().get(cacheKey);

            if (cached != null) {
                Long ttl = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
                if (ttl != null && shouldRefreshByPER(ttl, cached.computeTime(), 1.0)) {
                    specCacheRefreshService.refreshRankingCache(jobField, limit);
                }
            }

            if (cached == null) {
                cached = specRefreshQueryService.getRankingDataFromDb(jobField, limit);
                redisTemplate.opsForValue().set(cacheKey, cached, Duration.ofMinutes(10));
            }

            List<RankingResponse.RankingItem> items = cached.items().stream()
                    .map(i -> new RankingResponse.RankingItem(
                            i.userId(), i.nickname(), i.profileImageUrl(), i.specId(),
                            i.score(), i.totalRank(), i.totalUsersCount(),
                            i.jobField(), i.rankByJobField(), i.usersCountByJobField(),
                            i.commentsCount(), i.bookmarksCount()
                    ))
                    .toList();

            return new RankingData(items, cached.hasNext(), cached.nextCursor());

        } else {
            Long cursorId = decodeCursor(cursor);
            List<Spec> specs = specRepository.findTopSpecsByJobFieldWithCursor(jobField, cursorId, limit + 1);

            boolean hasNext = specs.size() > limit;
            if (hasNext) {
                specs = specs.subList(0, limit);
            }

            String nextCursor = hasNext ? encodeCursor(specs.getLast().getId()) : null;

            long countUsersHavingSpec = specRepository.countDistinctUsersHavingSpec();

            List<JobField> jobFields = new ArrayList<>();

            for (Spec spec : specs) {
                JobField jobField1 = spec.getJobField();
                jobFields.add(jobField1);
            }
            ArrayList<JobField> jobFields1 = new ArrayList<>(new HashSet<>(jobFields));
            List<Tuple> tuples = specRepository.countByJobFields(jobFields1);

            Map<JobField, Long> jobFieldCountMap = tuples.stream()
                    .collect(Collectors.toMap(
                            tuple -> tuple.get(spec.jobField),
                            tuple -> tuple.get(spec.count())
                    ));

            List<RankingResponse.RankingItem> rankingItems = specs.stream().map(spec -> {
                User user = spec.getUser();
                JobField specJobField = spec.getJobField();

                RankingItem rankingItem = new RankingItem(
                        user.getId(), user.getNickname(), user.getUserProfileUrl(), spec.getId(),
                        spec.getTotalAnalysisScore(),
                        specRepository.findAbsoluteRankByJobField(JobField.TOTAL, spec.getId()),
                        countUsersHavingSpec,
                        specJobField,
                        specRepository.findAbsoluteRankByJobField(specJobField, spec.getId()),
                        jobFieldCountMap.getOrDefault(specJobField, 0L),
                        commentRepository.countBySpecId(spec.getId()),
                        bookmarkRepository.countBySpecId(spec.getId())
                );
                return rankingItem;
            }).toList();

            return new RankingData(rankingItems, hasNext, nextCursor);
        }
    }

    public SearchData searchByNickname(String keyword, String cursor, int limit) {
        Long cursorId = decodeCursor(cursor);

        List<Spec> specs = specRepository.searchByNicknameWithCursor(keyword, cursorId, limit + 1);

        boolean hasNext = specs.size() > limit;
        if (hasNext) {
            specs = specs.subList(0, limit);
        }

        String nextCursor = null;
        if (hasNext) {
            nextCursor = encodeCursor(specs.getLast().getId());
        }

        Optional<Long> userId = UserUtils.getCurrentUserId();
        List<Long> bookmarkedSpecIds = new ArrayList<>();
        if (userId.isPresent()) {
            bookmarkedSpecIds = bookmarkRepository.findSpecIdsByUserId(userId.get());
        }

        List<SearchResponse.SearchResult> searchResults = new ArrayList<>();

        for (Spec spec : specs) {
            User user = spec.getUser();
            JobField jobField = spec.getJobField();

            Long currentRank = specRepository.findAbsoluteRankByJobField(JobField.TOTAL, spec.getId());
            Long jobFieldRank = specRepository.findAbsoluteRankByJobField(jobField, spec.getId());

            double averageScore = spec.getTotalAnalysisScore();

            Long commentsCount = commentRepository.countBySpecId(spec.getId());
            Long bookmarksCount = bookmarkRepository.countBySpecId(spec.getId());
            Long totalUserCount = userRepository.countUsersHavingSpec();
            Long totalUsersCountByJobField = specRepository.countByJobField(jobField);

            SearchResponse.SearchResult item = new SearchResponse.SearchResult(
                    user.getId(),
                    user.getNickname(),
                    user.getUserProfileUrl(),
                    spec.getId(),
                    averageScore,
                    currentRank,
                    totalUserCount,
                    jobField,
                    jobFieldRank,
                    totalUsersCountByJobField,
                    bookmarkedSpecIds.contains(spec.getId()),
                    commentsCount,
                    bookmarksCount
            );

            searchResults.add(item);
        }
        return new SearchData(keyword, searchResults, hasNext, nextCursor);
    }

    public Meta getMetaData(JobField jobField) {
        String cacheKey = "specMetadata::" + jobField.name();
        CachedMetaResponse cached = (CachedMetaResponse) redisTemplate.opsForValue()
                .get("specMetadata::" + jobField.name());

        if (cached != null) {
            Long ttl = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
            if (ttl != null && shouldRefreshByPER(ttl, cached.computeTime(), 1.0)) {
                specCacheRefreshService.refreshSpecMetadata(jobField);
            }
        }

        if (cached == null) {
            cached = specRefreshQueryService.getMetaDataFromDb(jobField);
            redisTemplate.opsForValue().set(cacheKey, cached, Duration.ofHours(1));
        }

        CachedMeta data = cached.data();

        return new Meta(data.totalUserCount(), data.averageScore());
    }

    private boolean shouldRefreshByPER(long ttl, long cacheComputeTime, double beta) {
        long remainedTtlMillis = TimeUnit.SECONDS.toMillis(ttl);
        double currentTime = System.currentTimeMillis();
        double randomNaturalLog = Math.log(Math.random());
        double expireTime = currentTime + remainedTtlMillis;

        return currentTime - cacheComputeTime * beta * randomNaturalLog >= expireTime;
    }
}
