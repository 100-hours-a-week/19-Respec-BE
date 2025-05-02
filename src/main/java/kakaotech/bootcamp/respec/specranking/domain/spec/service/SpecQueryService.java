package kakaotech.bootcamp.respec.specranking.domain.spec.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kakaotech.bootcamp.respec.specranking.domain.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.comment.repository.CommentRepository;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.RankingResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.dto.response.SearchResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
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

    public RankingResponse getRankings(String jobField, String cursor, int limit) {
        Long currentUserId = UserUtils.getCurrentUserId();
        Long cursorId = decodeCursor(cursor);

        List<Spec> specs = specRepository.findByJobFieldWithPagination(jobField, cursorId, limit + 1);

        boolean hasNext = specs.size() > limit;
        if (hasNext) {
            specs = specs.subList(0, limit);
        }

        String nextCursor = null;
        if (hasNext) {
            nextCursor = encodeCursor(specs.getLast().getId());
        }

        Set<Long> bookmarkedSpecIds = bookmarkRepository.findSpecIdsByUserId(currentUserId);

        Map<String, Integer> jobFieldUserCountMap = jobField != null ?
                Map.of(jobField, specRepository.countByJobField(jobField)) :
                specRepository.countByJobFields();

        List<RankingResponse.RankingItem> rankingItems = new ArrayList<>();

        int overallRank = 1;

        for (Spec spec : specs) {
            User user = spec.getUser();
            String specJobField = spec.getWorkPosition();

            int jobFieldRank = specRepository.findRankByJobField(spec.getId(), specJobField);

            double totalAnalysisScore = spec.getTotalAnalysisScore();

            int commentsCount = commentRepository.countBySpecId(spec.getId());
            int bookmarksCount = bookmarkRepository.countBySpecId(spec.getId());

            RankingResponse.RankingItem item = new RankingResponse.RankingItem();
            item.setUserId(user.getId());
            item.setNickname(user.getNickname());
            item.setProfileImageUrl(user.getUserProfileUrl());
            item.setSpecId(spec.getId());
            item.setJobField(specJobField);
            item.setTotalAnalyzeScore(totalAnalysisScore);
            item.setRankByJobField(jobFieldRank);
            item.setTotalUsersCountByJobField(jobFieldUserCountMap.getOrDefault(specJobField, 0));
            item.setRank(overallRank++);
            item.setBookmarked(bookmarkedSpecIds.contains(spec.getId()));
            item.setCommentsCount(commentsCount);
            item.setBookmarksCount(bookmarksCount);

            rankingItems.add(item);
        }

        return RankingResponse.success(rankingItems, hasNext, nextCursor);
    }

    public SearchResponse searchByNickname(String keyword, String cursor, int limit) {
        Long currentUserId = UserUtils.getCurrentUserId();

        Long cursorId = decodeCursor(cursor);

        List<Spec> specs = specRepository.searchByNickname(keyword, cursorId, limit + 1);

        boolean hasNext = specs.size() > limit;
        if (hasNext) {
            specs = specs.subList(0, limit);
        }

        String nextCursor = hasNext && !specs.isEmpty() ? encodeCursor(specs.get(specs.size() - 1).getId()) : null;

        Set<Long> bookmarkedSpecIds = bookmarkRepository.findSpecIdsByUserId(currentUserId);

        Map<String, Integer> jobFieldUserCountMap = specRepository.countByJobFields();

        List<SearchResponse.SearchResult> searchResults = new ArrayList<>();
        int overallRank = 1;

        for (Spec spec : specs) {
            User user = spec.getUser();
            String specJobField = spec.getWorkPosition();

            int jobFieldRank = specRepository.findRankByJobField(spec.getId(), specJobField);

            double averageScore = spec.getTotalAnalysisScore();

            int commentsCount = commentRepository.countBySpecId(spec.getId());

            int bookmarksCount = bookmarkRepository.countBySpecId(spec.getId());

            SearchResponse.SearchResult item = new SearchResponse.SearchResult();
            item.setUserId(user.getId());
            item.setNickname(user.getNickname());
            item.setProfileImageUrl(user.getUserProfileUrl());
            item.setSpecId(spec.getId());
            item.setJobField(specJobField);
            item.setTotalAnalyzeScore(averageScore);
            item.setRankByJobField(jobFieldRank);
            item.setTotalUsersCountByJobField(jobFieldUserCountMap.getOrDefault(specJobField, 0));
            item.setRank(overallRank++);
            item.setBookmarked(bookmarkedSpecIds.contains(spec.getId()));
            item.setCommentsCount(commentsCount);
            item.setBookmarksCount(bookmarksCount);

            searchResults.add(item);
        }

        return SearchResponse.success(keyword, searchResults, hasNext, nextCursor);
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