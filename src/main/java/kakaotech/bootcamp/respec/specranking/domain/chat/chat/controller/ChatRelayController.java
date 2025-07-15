package kakaotech.bootcamp.respec.specranking.domain.chat.chat.controller;

import static kakaotech.bootcamp.respec.specranking.domain.chat.chat.constant.ChatConstant.RELAY_REQUEST_SUCCESS_MESSAGE;

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
        Long senderId = chatRelayDto.senderId();
        Long receiverId = chatRelayDto.receiverId();
        log.info("메인 서버에 {}가 {}에게 보내는 메시지 요청이 들어왔습니다.", senderId, receiverId);
        chatService.sendMessageToUser(chatRelayDto);
        log.info("메인 서버에 {}가 {}에게 보내는 메시지 요청이 성공했습니다.", senderId, receiverId);
        return new SimpleResponseDto(true, RELAY_REQUEST_SUCCESS_MESSAGE);
    }
}
