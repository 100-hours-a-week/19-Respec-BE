package kakaotech.bootcamp.respec.specranking.domain.chatroom.util;

import java.util.ArrayList;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.chatroom.entity.Chatroom;
import kakaotech.bootcamp.respec.specranking.domain.chatroom.repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("chat-initialize")
@Order(3)
@RequiredArgsConstructor
public class InitializeChatroom implements CommandLineRunner {

    private final ChatroomRepository chatroomRepository;

    @Override
    public void run(String... args) {
        List<Chatroom> chatrooms = new ArrayList<>();

        // 50개의 채팅방 생성
        for (int i = 1; i <= 50; i++) {
            chatrooms.add(new Chatroom());
        }

        chatroomRepository.saveAll(chatrooms);

        System.out.println("50개의 채팅방 데이터가 성공적으로 생성되었습니다.");
    }
}
