package kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository;

import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.QSpec.spec;
import static kakaotech.bootcamp.respec.specranking.domain.user.entity.QUser.user;
import static kakaotech.bootcamp.respec.specranking.global.common.util.CursorUtils.isFirstCursor;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import org.springframework.stereotype.Repository;

@Repository
public class SpecRepositoryImpl implements SpecRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SpecRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Spec> findTopSpecsByJobFieldWithCursor(JobField jobField, Long cursorId, int limit) {

        if (isFirstCursor(cursorId)) {
            return queryFactory
                    .selectFrom(spec)
                    .where(
                            isActive(),
                            jobFieldEqualsWithTotalNull(jobField)
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
                        jobFieldEqualsWithTotalNull(jobField),
                        FilteringByCursor(cursorId, cursorScore)
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
                        jobFieldEqualsWithTotalNull(jobField)
                )
                .fetchOne();

        return count != null ? count : 0L;
    }

    public List<Tuple> countByJobFields(List<JobField> jobFields) {
        return queryFactory
                .select(spec.jobField, spec.count())
                .from(spec)
                .where(
                        isActive(),
                        jobFieldInWithNullSupport(jobFields)
                )
                .groupBy(spec.jobField)
                .fetch();
    }

    @Override
    public List<Spec> searchByNicknameWithCursor(String nickname, Long cursorId, int limit) {
        if (isFirstCursor(cursorId)) {
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
    public Long findAbsoluteRankByJobField(JobField jobField, Long specId) {

        Double targetScore = queryFactory
                .select(spec.totalAnalysisScore)
                .from(spec)
                .where(spec.id.eq(specId))
                .fetchOne();

        Long higherCount = queryFactory
                .select(spec.count())
                .from(spec)
                .where(
                        isActive(),
                        jobFieldEqualsWithTotalNull(jobField),
                        spec.totalAnalysisScore.gt(targetScore)
                )
                .fetchOne();

        return higherCount != null ? higherCount + 1 : 1;
    }

    @Override
    public Double findAverageScoreByJobField(JobField jobField) {
        return queryFactory
                .select(spec.totalAnalysisScore.avg())
                .from(spec)
                .where(
                        isActive(),
                        jobFieldEqualsWithTotalNull(jobField)
                )
                .fetchOne();
    }

    private BooleanExpression FilteringByCursor(Long cursorId, Double cursorScore) {
        return spec.totalAnalysisScore.lt(cursorScore)
                .or(
                        spec.totalAnalysisScore.eq(cursorScore)
                                .and(spec.id.lt(cursorId))
                );
    }

    private BooleanExpression isActive() {
        return spec.status.eq(SpecStatus.ACTIVE);
    }

    private BooleanExpression jobFieldEqualsWithTotalNull(JobField jobField) {
        if (jobField == null || jobField == JobField.TOTAL) {
            return null;
        }
        return spec.jobField.eq(jobField);
    }

    private BooleanExpression nicknameContains(String nickname) {
        return nickname != null && !nickname.isEmpty() ? user.nickname.contains(nickname) : null;
    }

    private BooleanExpression jobFieldInWithNullSupport(List<JobField> jobFields) {
        return spec.jobField.in(jobFields.stream().toList());
    }
}
