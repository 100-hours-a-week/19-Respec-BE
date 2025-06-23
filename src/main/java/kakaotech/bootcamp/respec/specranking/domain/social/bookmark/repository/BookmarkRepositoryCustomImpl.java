package kakaotech.bootcamp.respec.specranking.domain.social.bookmark.repository;

import static kakaotech.bootcamp.respec.specranking.domain.social.bookmark.entity.QBookmark.bookmark;
import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.QSpec.spec;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.social.bookmark.entity.Bookmark;
import kakaotech.bootcamp.respec.specranking.global.common.type.SpecStatus;
import org.springframework.stereotype.Repository;


@Repository
public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BookmarkRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Bookmark> findBookmarksByUserIdWithCursor(Long userId, Long cursorId, int limit) {
        if (cursorId == null) {
            return queryFactory
                    .selectFrom(bookmark)
                    .where(
                            bookmark.user.id.eq(userId),
                            spec.status.eq(SpecStatus.ACTIVE)
                    )
                    .orderBy(bookmark.id.desc())
                    .limit(limit)
                    .fetch();
        }

        return queryFactory
                .selectFrom(bookmark)
                .where(
                        bookmark.user.id.eq(userId),
                        spec.status.eq(SpecStatus.ACTIVE),
                        bookmark.id.lt(cursorId)
                )
                .orderBy(bookmark.id.desc())
                .limit(limit)
                .fetch();
    }
}
