package kakaotech.bootcamp.respec.specranking.domain.chat.chat.repository;


import static kakaotech.bootcamp.respec.specranking.domain.chat.chat.entity.QChat.chat;
import static kakaotech.bootcamp.respec.specranking.global.common.util.cursor.CursorUtils.isFirstCursor;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.entity.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Chat> findLatestChatsWithCursor(
            Long chatroomId,
            Long cursorId,
            int limitPlusOne
    ) {
        return queryFactory
                .selectFrom(chat)
                .where(chat.chatroom.id.eq(chatroomId),
                        buildCursorPredicate(cursorId))
                .orderBy(chat.createdAt.desc(), chat.id.desc())
                .limit(limitPlusOne)
                .fetch();
    }

    @Override
    public Optional<Chat> findLatestChatWithCursor(Long chatroomId) {
        Chat latestChat = queryFactory
                .selectFrom(chat)
                .where(chat.chatroom.id.eq(chatroomId))
                .orderBy(chat.createdAt.desc(), chat.id.desc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(latestChat);
    }

    private BooleanExpression buildCursorPredicate(Long cursorId) {
        if (isFirstCursor(cursorId)) {
            return null;
        }

        Chat cursorChat = queryFactory
                .selectFrom(chat)
                .where(chat.id.eq(cursorId))
                .fetchOne();

        if (cursorChat == null) {
            return null;
        }

        return chat.createdAt.lt(cursorChat.getCreatedAt())
                .or(
                        chat.createdAt.eq(cursorChat.getCreatedAt())
                                .and(chat.id.lt(cursorId))
                );
    }
}
