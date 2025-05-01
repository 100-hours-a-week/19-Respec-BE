package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.QSpec;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SpecRepositoryImpl implements SpecRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private final QSpec spec = QSpec.spec;
    private final QUser user = QUser.user;

    private JPAQueryFactory getQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Spec> findByJobFieldWithPagination(String jobField, Long cursorId, int limit) {
        return getQueryFactory()
                .selectFrom(spec)
                .where(
                        isActive(),
                        jobFieldEquals(jobField),
                        cursorLessThan(cursorId)
                )
                .orderBy(spec.totalAnalysisScore.desc())
                .limit(limit)
                .fetch();
    }
    
    @Override
    public int countByJobField(String jobField) {
        Long count = getQueryFactory()
                .select(spec.count())
                .from(spec)
                .where(
                        isActive(),
                        jobFieldEquals(jobField)
                )
                .fetchOne();
                
        return count != null ? count.intValue() : 0;
    }
    
    @Override
    public Map<String, Integer> countByJobFields() {
        List<Object[]> results = getQueryFactory()
                .select(spec.workPosition, spec.count())
                .from(spec)
                .where(isActive())
                .groupBy(spec.workPosition)
                .fetch()
                .stream()
                .map(tuple -> new Object[] { tuple.get(0, String.class), tuple.get(1, Long.class)})
                .toList();
                
        Map<String, Integer> countMap = new HashMap<>();
        for (Object[] result : results) {
            String jobField = (String) result[0];
            Long count = (Long) result[1];
            countMap.put(jobField, count.intValue());
        }
        
        return countMap;
    }
    
    @Override
    public int findRankByJobField(Long specId, String jobField) {
        // 해당 스펙의 점수 조회
        Double score = getQueryFactory()
                .select(spec.totalAnalysisScore)
                .from(spec)
                .where(spec.id.eq(specId))
                .fetchOne();
                
        if (score == null) {
            return 0;
        }
        
        // 해당 점수보다 높은 스펙의 수 + 1 (현재 랭킹)
        Long rank = getQueryFactory()
                .select(spec.count())
                .from(spec)
                .where(
                        isActive(),
                        jobFieldEquals(jobField),
                        spec.totalAnalysisScore.gt(score)
                )
                .fetchOne();
                
        return rank != null ? rank.intValue() + 1 : 1;
    }
    
    @Override
    public List<Spec> searchByNickname(String nickname, Long cursorId, int limit) {
        return getQueryFactory()
                .selectFrom(spec)
                .join(spec.user, user)
                .where(
                        isActive(),
                        nicknameContains(nickname),
                        cursorLessThan(cursorId)
                )
                .orderBy(spec.totalAnalysisScore.desc())
                .limit(limit)
                .fetch();
    }
    
    private BooleanExpression isActive() {
        return spec.status.eq(SpecStatus.ACTIVE);
    }
    
    private BooleanExpression jobFieldEquals(String jobField) {
        return jobField != null && !jobField.isEmpty() ? spec.workPosition.eq(jobField) : null;
    }
    
    private BooleanExpression cursorLessThan(Long cursorId) {
        return cursorId != null ? spec.id.lt(cursorId) : null;
    }
    
    private BooleanExpression nicknameContains(String nickname) {
        return nickname != null && !nickname.isEmpty() ? user.nickname.contains(nickname) : null;
    }
}
