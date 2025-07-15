package kakaotech.bootcamp.respec.specranking.domain.chat.chat.service;

import static kakaotech.bootcamp.respec.specranking.domain.auth.constant.AuthConstant.LOGIN_REQUIRED_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.chat.chat.constant.ChatConstant.GET_CHAT_LIST_SUCCESS_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.constant.ChatParticipationConstant.PARTNER_NOT_FOUND_MESSAGE;

import java.util.Base64;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.auth.exception.LoginRequiredException;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.response.ChatListResponse;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.response.ChatListResponse.ChatListData;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.response.ChatListResponse.ChatMessageDto;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.entity.Chat;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.repository.ChatRepository;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.entity.ChatParticipation;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.exception.ChatPartnerNotFoundException;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.repository.ChatParticipationRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

    private final ChatParticipationRepository chatParticipationRepository;
    private final ChatRepository chatRepository;

    public ChatListResponse fetchLastestChatMessages(Long chatroomId, String cursor, int limit) {
        Long cursorId = decodeCursor(cursor);

        Long loginUserId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new LoginRequiredException(LOGIN_REQUIRED_MESSAGE));

        List<ChatParticipation> participations =
                chatParticipationRepository.findByChatroomId(chatroomId);

        Long partnerId = participations.stream()
                .map(cp -> cp.getUser().getId())
                .filter(id -> !id.equals(loginUserId))
                .findFirst()
                .orElseThrow(() -> new ChatPartnerNotFoundException(PARTNER_NOT_FOUND_MESSAGE));

        List<Chat> chats = chatRepository
                .findLatestChatsWithCursor(chatroomId, cursorId, limit + 1);

        boolean hasNext = chats.size() > limit;
        if (hasNext) {
            chats = chats.subList(0, limit);
        }

        String nextCursor = null;
        if (hasNext) {
            nextCursor = encodeCursor(chats.getLast().getId());
        }

        List<ChatMessageDto> messageDtos = chats.stream()
                .map(chat -> new ChatMessageDto(
                        chat.getId(),
                        chat.getSender().getId(),
                        chat.getContent(),
                        chat.getCreatedAt().toString()
                ))
                .toList();

        ChatListData data = new ChatListData(partnerId, messageDtos, hasNext, nextCursor);

        return new ChatListResponse(true, GET_CHAT_LIST_SUCCESS_MESSAGE, data);
    }

    private String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(String.valueOf(id).getBytes());
    }

    private Long decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return Long.MAX_VALUE;
        }

        byte[] decodedBytes = Base64.getDecoder().decode(cursor);
        String decodedString = new String(decodedBytes);
        return Long.parseLong(decodedString);
    }
}
