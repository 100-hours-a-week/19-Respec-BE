package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import static kakaotech.bootcamp.respec.specranking.domain.spec.entity.QSpec.spec;
import static kakaotech.bootcamp.respec.specranking.domain.user.entity.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import org.springframework.stereotype.Repository;

@Repository
public class SpecRepositoryImpl implements SpecRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SpecRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Spec> findByJobFieldWithPagination(JobField jobField, Long cursorId, int limit) {
        boolean fetchAll = jobField == JobField.TOTAL;

        if (cursorId == null || cursorId == Long.MAX_VALUE) {
            return queryFactory
                    .selectFrom(spec)
                    .where(
                            isActive(),
                            fetchAll ? null : jobFieldEquals(jobField)
                    )
                    .orderBy(spec.totalAnalysisScore.desc(), spec.id.desc())
                    .limit(limit)
                    .fetch();
        }
        Double cursorScore = queryFactory
                .select(spec.totalAnalysisScore)
                .from(spec)
                .where(spec.id.eq(cursorId))
                .fetchOne();

        return queryFactory
                .selectFrom(spec)
                .where(
                        isActive(),
                        fetchAll ? null : jobFieldEquals(jobField),
                        spec.totalAnalysisScore.lt(cursorScore)
                                .or(
                                        spec.totalAnalysisScore.eq(cursorScore)
                                                .and(spec.id.lt(cursorId))
                                )
                )
                .orderBy(spec.totalAnalysisScore.desc(), spec.id.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Long countByJobField(JobField jobField) {
        Long count = queryFactory
                .select(spec.count())
                .from(spec)
                .where(
                        isActive(),
                        jobFieldEquals(jobField)
                )
                .fetchOne();

        return count != null ? count.longValue() : 0L;
    }

    @Override
    public Map<String, Long> countByJobFields() {
        List<Object[]> results = queryFactory
                .select(spec.jobField, spec.count())
                .from(spec)
                .where(isActive())
                .groupBy(spec.jobField)
                .fetch()
                .stream()
                .map(tuple -> new Object[]{tuple.get(0, JobField.class), tuple.get(1, Long.class)})
                .toList();

        Map<String, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
            JobField jobField = (JobField) result[0];
            Long count = (Long) result[1];
            countMap.put(jobField.getValue(), count);
        }

        return countMap;
    }

    @Override
    public Long findRankByJobField(Long specId, JobField jobField) {
        Double score = queryFactory
                .select(spec.totalAnalysisScore)
                .from(spec)
                .where(spec.id.eq(specId))
                .fetchOne();

        if (score == null) {
            return 0L;
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

        return rank != null ? rank + 1 : 1;
    }

    @Override
    public List<Spec> searchByNickname(String nickname, Long cursorId, int limit) {
        if (cursorId == null || cursorId == Long.MAX_VALUE) {
            return queryFactory
                    .selectFrom(spec)
                    .join(spec.user, user)
                    .where(
                            isActive(),
                            nicknameContains(nickname)
                    )
                    .orderBy(spec.id.desc())
                    .limit(limit)
                    .fetch();
        }

        return queryFactory
                .selectFrom(spec)
                .join(spec.user, user)
                .where(
                        isActive(),
                        nicknameContains(nickname),
                        spec.id.lt(cursorId)
                )
                .orderBy(spec.id.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Long findAbsoluteRank(JobField jobField, Long specId) {
        Double targetScore = queryFactory
                .select(spec.totalAnalysisScore)
                .from(spec)
                .where(spec.id.eq(specId))
                .fetchOne();

        boolean fetchAll = jobField == JobField.TOTAL;

        Long higherCount = queryFactory
                .select(spec.count())
                .from(spec)
                .where(
                        isActive(),
                        fetchAll ? null : jobFieldEquals(jobField),
                        spec.totalAnalysisScore.gt(targetScore)
                )
                .fetchOne();

        return higherCount + 1;
    }

    private BooleanExpression isActive() {
        return spec.status.eq(SpecStatus.ACTIVE);
    }

    private BooleanExpression jobFieldEquals(JobField jobField) {
        return jobField != null ? spec.jobField.eq(jobField) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return nickname != null && !nickname.isEmpty() ? user.nickname.contains(nickname) : null;
    }

    @Override
    public Long countDistinctUsersByJobField(JobField jobField) {
        Long count = queryFactory
                .select(spec.user.id.countDistinct())
                .from(spec)
                .where(spec.jobField.eq(jobField))
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public Double findAverageScoreByJobField(JobField jobField) {
        return queryFactory
                .select(spec.totalAnalysisScore.avg())
                .from(spec)
                .where(
                        jobField != null ? spec.jobField.eq(jobField) : null
                )
                .fetchOne();
    }

}
