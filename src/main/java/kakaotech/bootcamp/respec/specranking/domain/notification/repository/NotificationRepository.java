package kakaotech.bootcamp.respec.specranking.domain.notification.repository;

import kakaotech.bootcamp.respec.specranking.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
