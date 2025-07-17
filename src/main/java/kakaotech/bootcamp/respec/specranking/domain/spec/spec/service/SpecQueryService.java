package kakaotech.bootcamp.respec.specranking.domain.spec.spec.service;

import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.QSpec.spec;
import static kakaotech.bootcamp.respec.specranking.global.common.util.cursor.CursorUtils.decodeCursor;
import static kakaotech.bootcamp.respec.specranking.global.common.util.cursor.CursorUtils.encodeCursor;
import static kakaotech.bootcamp.respec.specranking.global.common.util.cursor.CursorUtils.processCursorPagination;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.SPEC_META_DATA_PREFIX;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.SPEC_RANKINGS_PREFIX;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.TOP_10_RANKINGS_CACHING_MINUTES;

import com.querydsl.core.Tuple;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedMetaDto;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedMetaDto.CachedMeta;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedRankingResponse;
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
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.util.cursor.CursorPagination;
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
            return getRankingForCache(jobField, limit);
        }
        return getRankingDataForBasic(jobField, cursor, limit);
    }

    private RankingData getRankingForCache(JobField jobField, int limit) {
        String cacheKey = SPEC_RANKINGS_PREFIX + jobField.name() + "::" + limit;
        CachedRankingResponse cached = (CachedRankingResponse) redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            Long ttl = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
            if (ttl != null && shouldRefreshByPER(ttl, cached.computeTime(), 1.0)) {
                specCacheRefreshService.refreshRankingCache(jobField, limit);
            }
        }

        if (cached == null) {
            cached = specRefreshQueryService.getRankingDataFromDb(jobField, limit);
            redisTemplate.opsForValue().set(cacheKey, cached, Duration.ofMinutes(TOP_10_RANKINGS_CACHING_MINUTES));
        }

        List<RankingItem> items = cached.items().stream()
                .map(i -> new RankingItem(
                        i.userId(), i.nickname(), i.profileImageUrl(), i.specId(),
                        i.score(), i.totalRank(), i.totalUsersCount(),
                        i.jobField(), i.rankByJobField(), i.usersCountByJobField(),
                        i.commentsCount(), i.bookmarksCount()
                ))
                .toList();

        return new RankingData(items, cached.hasNext(), cached.nextCursor());
    }

    private RankingData getRankingDataForBasic(JobField jobField, String cursor, int limit) {
        Long cursorId = decodeCursor(cursor);
        List<Spec> specs = specRepository.findTopSpecsByJobFieldWithCursor(jobField, cursorId, limit + 1);

        CursorPagination<Spec> cursorPagination = processCursorPagination(specs, limit, Spec::getId);
        boolean hasNext = cursorPagination.hasNext();
        specs = cursorPagination.items();
        String nextCursor = cursorPagination.nextCursor();

        long countUsersHavingSpec = specRepository.countDistinctUsersHavingSpec();

        List<JobField> jobFields = new ArrayList<>();

        for (Spec spec : specs) {
            JobField jobFieldBySpec = spec.getJobField();
            jobFields.add(jobFieldBySpec);
        }

        Map<JobField, Long> jobFieldCountMap = getJobFieldCountMap(
                jobFields);

        List<RankingItem> rankingItems = specs.stream().map(spec -> {
            User user = spec.getUser();
            JobField specJobField = spec.getJobField();

            RankingItem rankingItem = new RankingItem(
                    user.getId(), user.getNickname(), user.getUserProfileUrl(), spec.getId(),
                    spec.getTotalAnalysisScore(),
                    specRepository.findAbsoluteRankByJobField(JobField.TOTAL, spec.getId()),
                    countUsersHavingSpec, specJobField,
                    specRepository.findAbsoluteRankByJobField(specJobField, spec.getId()),
                    jobFieldCountMap.getOrDefault(specJobField, 0L),
                    commentRepository.countBySpecId(spec.getId()),
                    bookmarkRepository.countBySpecId(spec.getId())
            );
            return rankingItem;
        }).toList();

        return new RankingData(rankingItems, hasNext, nextCursor);
    }

    private Map<JobField, Long> getJobFieldCountMap(List<JobField> jobFields) {
        ArrayList<JobField> jobFieldsNotDuplicated = new ArrayList<>(new HashSet<>(jobFields));
        List<Tuple> tuples = specRepository.countByJobFields(jobFieldsNotDuplicated);

        return tuples.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(spec.jobField),
                        tuple -> tuple.get(spec.count())
                ));
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

        List<SearchResponse.SearchResult> searchResults = new ArrayList<>();
        Long totalUserCount = userRepository.countUsersHavingSpec();

        for (Spec spec : specs) {
            User user = spec.getUser();
            JobField jobField = spec.getJobField();

            Long currentRank = specRepository.findAbsoluteRankByJobField(JobField.TOTAL, spec.getId());
            Long jobFieldRank = specRepository.findAbsoluteRankByJobField(jobField, spec.getId());
            double averageScore = spec.getTotalAnalysisScore();

            Long commentsCount = commentRepository.countBySpecId(spec.getId());
            Long bookmarksCount = bookmarkRepository.countBySpecId(spec.getId());
            Long totalUsersCountByJobField = specRepository.countByJobField(jobField);

            SearchResponse.SearchResult item = new SearchResponse.SearchResult(
                    user.getId(), user.getNickname(), user.getUserProfileUrl(),
                    spec.getId(), averageScore, currentRank, totalUserCount,
                    jobField, jobFieldRank, totalUsersCountByJobField,
                    commentsCount, bookmarksCount
            );

            searchResults.add(item);
        }
        return new SearchData(keyword, searchResults, hasNext, nextCursor);
    }

    public Meta getMetaData(JobField jobField) {
        String cacheKey = SPEC_META_DATA_PREFIX + jobField.name();
        CachedMetaDto cached = (CachedMetaDto) redisTemplate.opsForValue()
                .get(SPEC_META_DATA_PREFIX + jobField.name());

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
