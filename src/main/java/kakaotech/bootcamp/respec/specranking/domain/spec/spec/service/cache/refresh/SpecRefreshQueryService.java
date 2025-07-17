package kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.cache.refresh;

import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.QSpec.spec;
import static kakaotech.bootcamp.respec.specranking.global.common.util.cursor.CursorUtils.decodeCursor;

import com.querydsl.core.Tuple;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedMetaDto;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedMetaDto.CachedMeta;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedRankingDto;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.query.SpecRankingsQueryService;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.query.SpecRankingsQueryService.RankingsBundle;
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
    private final SpecRankingsQueryService specRankingsQueryService;

    public CachedRankingDto getRankingDataFromDb(JobField jobField, int limit) {
        long startTime = System.currentTimeMillis();
        RankingsBundle rankingData = specRankingsQueryService.fetchRankingsBundle(jobField, decodeCursor(""), limit);

        List<Spec> specs = rankingData.specs();
        String nextCursor = rankingData.nextCursor();
        boolean hasNext = rankingData.hasNext();
        Map<JobField, Long> jobFieldCountMap = rankingData.jobFieldCountMap();
        long countUsersHavingSpec = rankingData.totalUsersCount();

        List<CachedRankingDto.CachedRankingItem> items = specs.stream().map(spec -> {
            User user = spec.getUser();
            JobField specJobField = spec.getJobField();

            return new CachedRankingDto.CachedRankingItem(
                    user.getId(), user.getNickname(), user.getUserProfileUrl(), spec.getId(),
                    spec.getTotalAnalysisScore(),
                    specRepository.findAbsoluteRankByJobField(JobField.TOTAL, spec.getId()),
                    countUsersHavingSpec, specJobField,
                    specRepository.findAbsoluteRankByJobField(specJobField, spec.getId()),
                    jobFieldCountMap.getOrDefault(specJobField, 0L),
                    commentRepository.countBySpecId(spec.getId()),
                    bookmarkRepository.countBySpecId(spec.getId())
            );
        }).toList();

        long endTime = System.currentTimeMillis();
        return new CachedRankingDto(items, hasNext, nextCursor, (endTime - startTime));
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

    public CachedMetaDto getMetaDataFromDb(JobField jobField) {
        long startTime = System.currentTimeMillis();
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

        long endTime = System.currentTimeMillis();
        CachedMeta cachedMeta = new CachedMeta(totalUserCount, averageScore);
        return new CachedMetaDto(endTime - startTime, cachedMeta);
    }

}
