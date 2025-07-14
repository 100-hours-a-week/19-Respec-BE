package kakaotech.bootcamp.respec.specranking.global.devsetup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.entity.ChatParticipation;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.repository.ChatParticipationRepository;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatroom.entity.Chatroom;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatroom.repository.ChatroomRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("chat-initialize")
@Order(4)
@RequiredArgsConstructor
@Slf4j
public class InitializeChatParticipation implements CommandLineRunner {

    private final ChatParticipationRepository chatParticipationRepository;
    private final UserRepository userRepository;
    private final ChatroomRepository chatroomRepository;
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        List<User> users = userRepository.findAll();
        List<Chatroom> chatrooms = chatroomRepository.findAll();

        List<ChatParticipation> participations = assignParticipantsToChatrooms(chatrooms, users);
        chatParticipationRepository.saveAll(participations);

        log.info("{}개의 채팅 참여 데이터가 성공적으로 생성되었습니다.", participations.size());
    }

    private List<ChatParticipation> assignParticipantsToChatrooms(List<Chatroom> chatrooms, List<User> users) {
        List<ChatParticipation> participations = new ArrayList<>();

        for (Chatroom chatroom : chatrooms) {
            List<User> selectedUsers = pickTwoDistinctUsers(users);
            for (User user : selectedUsers) {
                participations.add(new ChatParticipation(chatroom, user));
            }
        }

        return participations;
    }

    private List<User> pickTwoDistinctUsers(List<User> users) {
        User user1 = users.get(random.nextInt(users.size()));
        User user2;

        do {
            user2 = users.get(random.nextInt(users.size()));
        } while (user1.getId().equals(user2.getId()));

        return List.of(user1, user2);
    }
}
