package kakaotech.bootcamp.respec.specranking.domain.chat.repository;

import kakaotech.bootcamp.respec.specranking.domain.chat.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
}
