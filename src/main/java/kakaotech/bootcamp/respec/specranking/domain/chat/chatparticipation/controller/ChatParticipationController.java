package kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.controller;

import static kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.constant.ChatParticipationConstant.GET_CHAT_PARTICIPATIONS_SUCCESS_MESSAGE;

import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.dto.response.ChatParticipationListResponse;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.dto.response.ChatParticipationListResponse.ChatParticipationListData;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.service.ChatParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-participations")
@RequiredArgsConstructor
public class ChatParticipationController {

    private final ChatParticipationService chatParticipationService;

    @GetMapping
    public ChatParticipationListResponse getChatParticipationList() {
        ChatParticipationListData chatParticipationList = chatParticipationService.getChatParticipationList();
        return new ChatParticipationListResponse(true, GET_CHAT_PARTICIPATIONS_SUCCESS_MESSAGE, chatParticipationList);
    }
}
