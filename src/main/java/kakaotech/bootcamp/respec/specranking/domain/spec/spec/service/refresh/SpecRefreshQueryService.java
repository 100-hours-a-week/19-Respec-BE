package kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.refresh;

import java.util.Base64;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.cache.CachedRankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.response.SpecMetaResponse.Meta;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
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
        long startTime = System.currentTimeMillis();
        List<Spec> specs = specRepository.findTopSpecsByJobFieldWithCursor(jobField, Long.MAX_VALUE, limit + 1);

        boolean hasNext = specs.size() > limit;
        if (hasNext) {
            specs = specs.subList(0, limit);
        }

        String nextCursor = null;
        if (hasNext) {
            nextCursor = encodeCursor(specs.getLast().getId());
        }

        List<CachedRankingResponse.CachedRankingItem> items = specs.stream().map(spec -> {
            User user = spec.getUser();
            JobField specJobField = spec.getJobField();

            Long totalRank = specRepository.findAbsoluteRankByJobField(JobField.TOTAL, spec.getId());
            Long jobFieldRank = specRepository.findAbsoluteRankByJobField(specJobField, spec.getId());
            Long totalUserCount = userRepository.countUsersHavingSpec();
            Long usersCountByJobField = specRepository.countByJobField(specJobField);
            Long commentsCount = commentRepository.countBySpecId(spec.getId());
            Long bookmarksCount = bookmarkRepository.countBySpecId(spec.getId());

            return new CachedRankingResponse.CachedRankingItem(
                    user.getId(),
                    user.getNickname(),
                    user.getUserProfileUrl(),
                    spec.getId(),
                    spec.getTotalAnalysisScore(),
                    totalRank,
                    totalUserCount,
                    specJobField,
                    jobFieldRank,
                    usersCountByJobField,
                    commentsCount,
                    bookmarksCount
            );
        }).toList();

        long endTime = System.currentTimeMillis();
        return new CachedRankingResponse(items, hasNext, nextCursor, (endTime - startTime));
    }

    private String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(String.valueOf(id).getBytes());
    }
}
