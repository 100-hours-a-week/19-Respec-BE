package kakaotech.bootcamp.respec.specranking.domain.chat.chatroom.repository;

import kakaotech.bootcamp.respec.specranking.domain.chat.chatroom.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
}
