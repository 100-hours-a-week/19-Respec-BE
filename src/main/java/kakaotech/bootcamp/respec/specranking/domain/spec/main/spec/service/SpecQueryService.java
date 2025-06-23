package kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.social.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.dto.response.RankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.dto.response.SearchResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.dto.response.SpecMetaResponse.Meta;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.main.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import lombok.RequiredArgsConstructor;
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

    public RankingResponse getRankings(JobField jobField, String cursor, int limit) {
        Long cursorId = decodeCursor(cursor);

        List<Spec> specs = specRepository.findTopSpecsByJobFieldWithCursor(jobField, cursorId, limit + 1);

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

        List<RankingResponse.RankingItem> rankingItems = new ArrayList<>();

        for (Spec spec : specs) {
            User user = spec.getUser();
            JobField specJobField = spec.getJobField();

            Long totalRank = specRepository.findAbsoluteRankByJobField(JobField.TOTAL, spec.getId());
            Long jobFieldRank = specRepository.findAbsoluteRankByJobField(specJobField, spec.getId());

            Double totalAnalysisScore = spec.getTotalAnalysisScore();

            Long commentsCount = commentRepository.countBySpecId(spec.getId());
            Long bookmarksCount = bookmarkRepository.countBySpecId(spec.getId());
            Long totalUserCount = userRepository.countUsersHavingSpec();
            Long usersCountByJobField = specRepository.countByJobField(specJobField);

            RankingResponse.RankingItem item = new RankingResponse.RankingItem(
                    user.getId(), user.getNickname(),
                    user.getUserProfileUrl(), spec.getId(),
                    totalAnalysisScore, totalRank, totalUserCount,
                    specJobField, jobFieldRank, usersCountByJobField,
                    bookmarkedSpecIds.contains(spec.getId()),
                    commentsCount, bookmarksCount);

            rankingItems.add(item);
        }

        return RankingResponse.success(rankingItems, hasNext, nextCursor);
    }

    public SearchResponse searchByNickname(String keyword, String cursor, int limit) {
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

        return SearchResponse.success(keyword, searchResults, hasNext, nextCursor);
    }

    public Meta getMetaData(JobField jobField) {
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

    private String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(String.valueOf(id).getBytes());
    }

    private Long decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return Long.MAX_VALUE;
        }

        byte[] decodedBytes = Base64.getDecoder().decode(cursor);
        String decodedString = new String(decodedBytes);
        return Long.parseLong(decodedString);
    }


}
