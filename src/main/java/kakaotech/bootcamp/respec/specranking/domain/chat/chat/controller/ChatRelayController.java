package kakaotech.bootcamp.respec.specranking.domain.chat.chat.controller;

import java.io.IOException;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.request.ChatRelayRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.service.ChatService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRelayController {

    private final ChatService chatService;

    @PostMapping("/relay")
    public SimpleResponseDto relayMessage(@RequestBody ChatRelayRequestDto chatRelayDto) throws IOException {
        chatService.sendMessageToUser(chatRelayDto);
        return new SimpleResponseDto(true, "relay 요청 성공");
    }
}
