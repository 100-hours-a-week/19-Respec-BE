package kakaotech.bootcamp.respec.specranking.domain.chatparticipation.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kakaotech.bootcamp.respec.specranking.domain.chatparticipation.entity.ChatParticipation;
import kakaotech.bootcamp.respec.specranking.domain.chatparticipation.repository.ChatParticipationRepository;
import kakaotech.bootcamp.respec.specranking.domain.chatroom.entity.Chatroom;
import kakaotech.bootcamp.respec.specranking.domain.chatroom.repository.ChatroomRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("chat-initialize")
@Order(4)
@RequiredArgsConstructor
public class InitializeChatParticipation implements CommandLineRunner {

    private final ChatParticipationRepository chatParticipationRepository;
    private final UserRepository userRepository;
    private final ChatroomRepository chatroomRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<User> users = userRepository.findAll();
        List<Chatroom> chatrooms = chatroomRepository.findAll();
        List<ChatParticipation> participations = new ArrayList<>();

        Random random = new Random();

        // 각 채팅방에 2명씩 참여시키기
        for (Chatroom chatroom : chatrooms) {
            // 랜덤하게 2명의 사용자 선택
            User user1 = users.get(random.nextInt(users.size()));
            User user2;
            
            // 다른 사용자 선택 (같은 사용자가 두 번 들어가지 않도록)
            do {
                user2 = users.get(random.nextInt(users.size()));
            } while (user1.getId().equals(user2.getId()));

            participations.add(new ChatParticipation(chatroom, user1));
            participations.add(new ChatParticipation(chatroom, user2));
        }

        chatParticipationRepository.saveAll(participations);

        System.out.println(participations.size() + "개의 채팅 참여 데이터가 성공적으로 생성되었습니다.");
        System.out.println("각 채팅방당 2명씩 참여, 총 " + chatrooms.size() + "개의 채팅방");
    }
}
