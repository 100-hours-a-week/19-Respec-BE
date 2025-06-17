package kakaotech.bootcamp.respec.specranking.domain.chatparticipation.controller;

import kakaotech.bootcamp.respec.specranking.domain.chatparticipation.dto.response.ChatParticipationListResponse;
import kakaotech.bootcamp.respec.specranking.domain.chatparticipation.service.ChatParticipationService;
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
        return chatParticipationService.getChatParticipationList();
    }
}
