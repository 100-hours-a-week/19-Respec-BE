package kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.entity.Chat;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.repository.ChatRepository;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.dto.response.ChatParticipationListResponse;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.dto.response.ChatParticipationListResponse.ChatParticipationListData;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.dto.response.ChatParticipationListResponse.ChatRoomDto;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.entity.ChatParticipation;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.repository.ChatParticipationRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
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

    public ChatParticipationListResponse getChatParticipationList() {
        Long loginUserId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

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
                            .orElseThrow(() -> new IllegalArgumentException("채팅에 참여한 상대 사용자 정보를 찾을 수 없습니다."));

                    User partner = userRepository.findById(partnerId)
                            .orElseThrow(() -> new IllegalArgumentException("파트너 정보를 찾을 수 없습니다."));

                    Chat lastChat = chatRepository.findLatestChatWithCursor(chatroomId)
                            .orElseThrow(() -> new IllegalArgumentException("채팅이 없는데 채팅방이 있을 수 없습니다."));

                    String lastMessage = lastChat.getContent();
                    LocalDateTime lastMessageTime = lastChat.getCreatedAt();

                    return new ChatRoomDto(
                            chatroomId,
                            partnerId,
                            partner.getNickname(),
                            partner.getUserProfileUrl(),
                            lastMessage,
                            lastMessageTime
                    );
                })
                .collect(Collectors.toList());

        ChatParticipationListData data = new ChatParticipationListData(chatroomDtos);

        return new ChatParticipationListResponse(true, "채팅방 목록 조회 성공", data);
    }
}
