package kakaotech.bootcamp.respec.specranking.global.dev.sql;

import java.util.ArrayList;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class InitializeUser implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        List<User> users = new ArrayList<>();

        users.add(createUser("user1", "개발왕1", "https://picsum.photos/200"));
        users.add(createUser("user2", "백엔드고수2", "https://picsum.photos/200"));
        users.add(createUser("user3", "프론트엔드3", "https://picsum.photos/200"));
        users.add(createUser("user4", "디자이너4", "https://picsum.photos/200"));
        users.add(createUser("user5", "기획자5", "https://picsum.photos/200"));
        users.add(createUser("user6", "마케터6", "https://picsum.photos/200"));
        users.add(createUser("user7", "PM7", "https://picsum.photos/200"));
        users.add(createUser("user8", "데이터분석가8", "https://picsum.photos/200"));
        users.add(createUser("user9", "클라우드엔지니어9", "https://picsum.photos/200"));
        users.add(createUser("user10", "보안전문가10", "https://picsum.photos/200"));

        userRepository.saveAll(users);

        System.out.println("10명의 사용자 데이터가 성공적으로 생성되었습니다.");
    }

    private User createUser(String userId, String nickname, String profileUrl) {
        return new User(
                userId,
                "1111",
                profileUrl,
                nickname,
                true
        );
    }
}
