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
            // 현재 사용자 ID 가져오기
            Long currentUserId = UserUtils.getCurrentUserId();
            
            // 커서 디코딩
            Long cursorId = decodeCursor(cursor);
            
            // 닉네임으로 검색
            List<Spec> specs = specRepository.searchByNickname(keyword, cursorId, limit + 1);
            
            // 다음 페이지 여부 확인
            boolean hasNext = specs.size() > limit;
            if (hasNext) {
                specs = specs.subList(0, limit);
            }
            
            // 다음 커서 계산
            String nextCursor = hasNext && !specs.isEmpty() ? encodeCursor(specs.get(specs.size() - 1).getId()) : null;
            
            // 북마크 정보 조회
            Set<Long> bookmarkedSpecIds = bookmarkRepository.findSpecIdsByUserId(currentUserId);
            
            // 직무별 전체 사용자 수 조회
            Map<String, Integer> jobFieldUserCountMap = specRepository.countByJobFields();
            
            // 검색 결과 변환
            List<SearchResponse.SearchResult> searchResults = new ArrayList<>();
            int overallRank = 1;
            
            for (Spec spec : specs) {
                User user = spec.getUser();
                String specJobField = spec.getWorkPosition();
                
                // 직무별 랭킹 계산
                int jobFieldRank = specRepository.findRankByJobField(spec.getId(), specJobField);
                
                // 평균 점수 계산
                double averageScore = calculateAverageScore(spec);
                
                // 댓글 수 조회
                int commentsCount = commentRepository.countBySpecId(spec.getId());
                
                // 북마크 수 조회
                int bookmarksCount = bookmarkRepository.countBySpecId(spec.getId());
                
                // 검색 결과 아이템 생성
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
            return Long.MAX_VALUE; // 첫 페이지인 경우 가장 큰 ID부터 시작
        }
        
        try {
            // Base64 디코딩 후 ID 변환
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
