package kakaotech.bootcamp.respec.specranking.domain.chat.chat.repository;

import kakaotech.bootcamp.respec.specranking.domain.chat.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long>, ChatRepositoryCustom {
}
