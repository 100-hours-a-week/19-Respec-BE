package kakaotech.bootcamp.respec.specranking.domain.user.repository;

import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.QSpec.spec;
import static kakaotech.bootcamp.respec.specranking.domain.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public long countUsersHavingSpec() {
        Long result = queryFactory
                .select(user.id.countDistinct())
                .from(user)
                .join(spec).on(spec.user.id.eq(user.id))
                .fetchOne();

        return result != null ? result : 0L;
    }
}
