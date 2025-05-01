package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.QSpec;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SpecRepositoryImpl implements SpecRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QSpec spec = QSpec.spec;

    @Override
    public List<Spec> findByJobFieldWithPagination(String jobField, Long cursorId, int limit) {
        return queryFactory
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
        Long count = queryFactory
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
        List<Object[]> results = queryFactory
                .select(spec.workPosition, spec.count())
                .from(spec)
                .where(isActive())
                .groupBy(spec.workPosition)
                .fetch()
                .stream()
                .map(tuple -> new Object[]{tuple.get(0, String.class), tuple.get(1, Long.class)})
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
        Double score = queryFactory
                .select(spec.totalAnalysisScore)
                .from(spec)
                .where(spec.id.eq(specId))
                .fetchOne();

        if (score == null) {
            return 0;
        }

        Long rank = queryFactory
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

    private BooleanExpression isActive() {
        return spec.status.eq(SpecStatus.ACTIVE);
    }

    private BooleanExpression jobFieldEquals(String jobField) {
        return jobField != null && !jobField.isEmpty() ? spec.workPosition.eq(jobField) : null;
    }

    private BooleanExpression cursorLessThan(Long cursorId) {
        return cursorId != null ? spec.id.lt(cursorId) : null;
    }
}
