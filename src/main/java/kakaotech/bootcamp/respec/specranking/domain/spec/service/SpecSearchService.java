package kakaotech.bootcamp.respec.specranking.domain.spec.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kakaotech.bootcamp.respec.specranking.domain.bookmark.repository.BookmarkRepository;
import kakaotech.bootcamp.respec.specranking.domain.comment.repository.CommentRepository;
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
public class SpecSearchService {

    private final SpecRepository specRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;

    public SearchResponse searchByNickname(String keyword, String cursor, int limit) {
        try {
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

                double averageScore = calculateAverageScore(spec);

                int commentsCount = commentRepository.countBySpecId(spec.getId());

                int bookmarksCount = bookmarkRepository.countBySpecId(spec.getId());

                SearchResponse.SearchResult item = new SearchResponse.SearchResult();
                item.setUserId(user.getId());
                item.setNickname(user.getNickname());
                item.setProfileImageUrl(user.getUserProfileUrl());
                item.setSpecId(spec.getId());
                item.setJobField(specJobField);
                item.setAverageScore(averageScore);
                item.setRankByJobField(jobFieldRank);
                item.setTotalUsersCountByJobField(jobFieldUserCountMap.getOrDefault(specJobField, 0));
                item.setRank(overallRank++);
                item.setBookmarked(bookmarkedSpecIds.contains(spec.getId()));
                item.setCommentsCount(commentsCount);
                item.setBookmarksCount(bookmarksCount);

                searchResults.add(item);
            }

            return SearchResponse.success(keyword, searchResults, hasNext, nextCursor);
        } catch (Exception e) {
            e.printStackTrace();
            return SearchResponse.fail("검색 중 오류가 발생했습니다.");
        }
    }

    private double calculateAverageScore(Spec spec) {
        return (spec.getEducationScore()
                + spec.getWorkExperienceScore()
                + spec.getActivityNetworkingScore()
                + spec.getCertificationScore()
                + spec.getEnglishSkillScore()) / 5.0;
    }

    private Long decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return Long.MAX_VALUE;
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(cursor);
            String decodedString = new String(decodedBytes);
            return Long.parseLong(decodedString);
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    private String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(String.valueOf(id).getBytes());
    }
}
