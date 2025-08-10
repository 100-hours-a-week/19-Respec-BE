package kakaotech.bootcamp.respec.specranking.domain.chat.chat.controller;

import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.response.ChatListResponse;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.service.ChatQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatrooms/{chatroomId}/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatQueryService chatQueryService;

    @GetMapping
    public ChatListResponse getChatMessages(
            @PathVariable("chatroomId") Long chatroomId,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return chatQueryService.fetchLastestChatMessages(chatroomId, cursor, limit);
    }
}
