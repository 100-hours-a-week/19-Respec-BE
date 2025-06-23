package kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.repository;

import static kakaotech.bootcamp.respec.specranking.domain.chat.chat.entity.QChat.chat;
import static kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.entity.QChatParticipation.chatParticipation;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.entity.ChatParticipation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatParticipationRepositoryImpl implements ChatParticipationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatParticipation> findAllByUserIdOrderByLastChatroomMessage(Long userId) {
        return queryFactory
                .selectFrom(chatParticipation)
                .where(
                        chatParticipation.user.id.eq(userId),
                        chatParticipation.deletedAt.isNull()
                )
                .orderBy(
                        new OrderSpecifier<>(
                                Order.DESC,
                                JPAExpressions
                                        .select(chat.createdAt.max())
                                        .from(chat)
                                        .where(chat.chatroom.eq(chatParticipation.chatroom))
                        ),
                        chatParticipation.id.desc()
                )
                .fetch();
    }
}
