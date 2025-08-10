package kakaotech.bootcamp.respec.specranking.global.devsetup;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatroom.entity.Chatroom;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatroom.repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("chat-initialize")
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class InitializeChatroom implements CommandLineRunner {

    private static final int CHATROOM_COUNT = 50;

    private final ChatroomRepository chatroomRepository;

    @Override
    public void run(String... args) {
        List<Chatroom> chatrooms = generateChatrooms(CHATROOM_COUNT);
        chatroomRepository.saveAll(chatrooms);
        log.info("{}개의 채팅방 데이터가 성공적으로 생성되었습니다.", CHATROOM_COUNT);
    }

    private List<Chatroom> generateChatrooms(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> new Chatroom())
                .collect(Collectors.toList());
    }
}
