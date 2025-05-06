package kakaotech.bootcamp.respec.specranking.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.QSpec;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.QUser;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private final QSpec spec = QSpec.spec;
    private final QUser user = QUser.user;

    private JPAQueryFactory getQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Override
    public long countUsersHavingSpec() {
        Long result = getQueryFactory()
                .select(user.id.countDistinct())
                .from(user)
                .join(spec).on(spec.user.id.eq(user.id))
                .fetchOne();
        return result != null ? result : 0L;
    }
}
