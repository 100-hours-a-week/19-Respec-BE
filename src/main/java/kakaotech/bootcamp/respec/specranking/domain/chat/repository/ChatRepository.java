package kakaotech.bootcamp.respec.specranking.domain.chat.repository;

import kakaotech.bootcamp.respec.specranking.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
