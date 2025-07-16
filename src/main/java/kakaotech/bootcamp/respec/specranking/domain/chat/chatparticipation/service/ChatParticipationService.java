package kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.service;

import static kakaotech.bootcamp.respec.specranking.domain.auth.constant.AuthConstant.LOGIN_REQUIRED_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.constant.ChatParticipationConstant.CHAT_NOT_FOUND_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.constant.ChatParticipationConstant.PARTNER_NOT_FOUND_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.user.constants.UserConstant.USER_NOT_FOUND_MESSAGE;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.auth.exception.LoginRequiredException;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.entity.Chat;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.repository.ChatRepository;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.dto.response.ChatParticipationListResponse.ChatParticipationListData;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.dto.response.ChatParticipationListResponse.ChatRoomDto;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.entity.ChatParticipation;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.exception.ChatNotFoundException;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.exception.ChatPartnerNotFoundException;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.repository.ChatParticipationRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.exception.UserNotFoundException;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatParticipationService {

    private final ChatParticipationRepository chatParticipationRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatParticipationListData getChatParticipationList() {
        Long loginUserId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new LoginRequiredException(LOGIN_REQUIRED_MESSAGE));

        List<ChatParticipation> participations = chatParticipationRepository
                .findAllByUserIdOrderByLastChatroomMessage(loginUserId);

        List<ChatRoomDto> chatroomDtos = participations.stream()
                .map(participation -> {
                    Long chatroomId = participation.getChatroom().getId();

                    List<ChatParticipation> allParticipations =
                            chatParticipationRepository.findByChatroomId(chatroomId);

                    Long partnerId = allParticipations.stream()
                            .map(cp -> cp.getUser().getId())
                            .filter(id -> !id.equals(loginUserId))
                            .findFirst()
                            .orElseThrow(() -> new ChatPartnerNotFoundException(PARTNER_NOT_FOUND_MESSAGE));

                    User partner = userRepository.findById(partnerId)
                            .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

                    Chat lastChat = chatRepository.findLatestChatWithCursor(chatroomId)
                            .orElseThrow(() -> new ChatNotFoundException(CHAT_NOT_FOUND_MESSAGE));

                    return new ChatRoomDto(chatroomId, partnerId, partner.getNickname(),
                            partner.getUserProfileUrl(), lastChat.getContent(), lastChat.getCreatedAt());
                })
                .toList();

        return new ChatParticipationListData(chatroomDtos);
    }
}
