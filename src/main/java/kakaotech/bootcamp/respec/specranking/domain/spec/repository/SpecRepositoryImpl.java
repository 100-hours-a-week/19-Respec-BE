package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.common.type.SpecStatus;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.QSpec;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

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
        if (cursorId == null || cursorId == Long.MAX_VALUE) {
            return getQueryFactory()
                    .selectFrom(spec)
                    .where(
                            isActive(),
                            jobFieldEquals(jobField)
                    )
                    .orderBy(spec.totalAnalysisScore.desc(), spec.id.desc())
                    .limit(limit)
                    .fetch();
        }
        Double cursorScore = getQueryFactory()
                .select(spec.totalAnalysisScore)
                .from(spec)
                .where(spec.id.eq(cursorId))
                .fetchOne();

        return getQueryFactory()
                .selectFrom(spec)
                .where(
                        isActive(),
                        jobFieldEquals(jobField),
                        spec.totalAnalysisScore.eq(cursorScore),
                        spec.id.lt(cursorId)
                )
                .orderBy(spec.id.desc())
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
                .map(tuple -> new Object[]{tuple.get(0, JobField.class), tuple.get(1, Long.class)})
                .toList();

        Map<String, Integer> countMap = new HashMap<>();
        for (Object[] result : results) {
            JobField jobField = (JobField) result[0];
            Long count = (Long) result[1];
            countMap.put(jobField.getValue(), count.intValue());
        }

        return countMap;
    }

    @Override
    public int findRankByJobField(Long specId, String jobField) {
        Double score = getQueryFactory()
                .select(spec.totalAnalysisScore)
                .from(spec)
                .where(spec.id.eq(specId))
                .fetchOne();

        if (score == null) {
            return 0;
        }

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
        if (cursorId == null || cursorId == Long.MAX_VALUE) {
            return getQueryFactory()
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

        return getQueryFactory()
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
    public int findAbsoluteRank(String jobField, Long specId) {
        Double targetScore = getQueryFactory()
                .select(spec.totalAnalysisScore)
                .from(spec)
                .where(spec.id.eq(specId))
                .fetchOne();

        if (targetScore == null) {
            return 0;
        }

        Long higherCount = getQueryFactory()
                .select(spec.count())
                .from(spec)
                .where(
                        isActive(),
                        jobFieldEquals(jobField),
                        spec.totalAnalysisScore.gt(targetScore)
                )
                .fetchOne();

        return (higherCount != null ? higherCount.intValue() : 0) + 1;
    }

    private BooleanExpression isActive() {
        return spec.status.eq(SpecStatus.ACTIVE);
    }

    private BooleanExpression jobFieldEquals(String jobFieldStr) {
        try {
            if (jobFieldStr != null && !jobFieldStr.isEmpty()) {
                JobField jobField = JobField.valueOf(jobFieldStr);
                return spec.workPosition.eq(jobField);
            }
        } catch (IllegalArgumentException e) {
            // 존재하지 않는 JobField 값이 넘어온 경우
        }
        return null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return nickname != null && !nickname.isEmpty() ? user.nickname.contains(nickname) : null;
    }

}
