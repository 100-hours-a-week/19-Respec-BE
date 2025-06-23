package kakaotech.bootcamp.respec.specranking.global.devsetup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.entity.Chat;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.repository.ChatRepository;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.entity.ChatParticipation;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.repository.ChatParticipationRepository;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatroom.entity.Chatroom;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("chat-initialize")
@Order(5)
@RequiredArgsConstructor
public class InitializeChat implements CommandLineRunner {

    private final ChatRepository chatRepository;
    private final ChatParticipationRepository chatParticipationRepository;

    private final String[] sampleMessages = {
            "안녕하세요!",
            "반갑습니다.",
            "오늘 날씨가 좋네요.",
            "프로젝트 어떻게 진행되고 있나요?",
            "회의 시간 조율 가능하신가요?",
            "자료 확인했습니다. 감사합니다!",
            "내일 점심 같이 드실래요?",
            "코드 리뷰 완료했습니다.",
            "질문이 있는데 시간 괜찮으신가요?",
            "업무 처리 완료했습니다.",
            "네, 알겠습니다!",
            "좋은 아이디어네요.",
            "확인 후 연락드리겠습니다.",
            "수고하셨습니다.",
            "다음 주 일정 어떻게 되나요?",
            "문서 공유해주세요.",
            "테스트 결과 확인했습니다.",
            "피드백 반영하겠습니다.",
            "미팅룸 예약했습니다.",
            "늦어서 죄송합니다."
    };

    @Override
    @Transactional
    public void run(String... args) {
        List<ChatParticipation> participations = chatParticipationRepository.findAll();
        List<Chat> chats = new ArrayList<>();
        Random random = new Random();

        // 채팅방별로 그룹화
        participations.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        cp -> cp.getChatroom().getId()
                ))
                .forEach((chatroomId, roomParticipations) -> {
                    if (roomParticipations.size() >= 2) {
                        Chatroom chatroom = roomParticipations.get(0).getChatroom();
                        User user1 = roomParticipations.get(0).getUser();
                        User user2 = roomParticipations.get(1).getUser();

                        // 각 채팅방당 5-15개의 메시지 생성
                        int messageCount = 5 + random.nextInt(11); // 5~15개
                        LocalDateTime baseTime = LocalDateTime.now().minusDays(random.nextInt(30));

                        for (int i = 0; i < messageCount; i++) {
                            // 랜덤하게 보내는 사람 선택
                            User sender = random.nextBoolean() ? user1 : user2;
                            User receiver = sender.equals(user1) ? user2 : user1;

                            String content = sampleMessages[random.nextInt(sampleMessages.length)];

                            Chat chat = new Chat(sender, receiver, chatroom, content);
                            chats.add(chat);

                            baseTime = baseTime.plusMinutes(random.nextInt(60) + 10); // 10분~70분 간격
                        }
                    }
                });

        chatRepository.saveAll(chats);

        System.out.println(chats.size() + "개의 채팅 메시지 데이터가 성공적으로 생성되었습니다.");
    }
}
