package kakaotech.bootcamp.respec.specranking.domain.notification.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.global.common.type.NotificationTargetType;
import kakaotech.bootcamp.respec.specranking.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    boolean existsByUserIdAndTargetName(Long userId, NotificationTargetType targetName);
    
    List<Notification> findByUserIdAndTargetName(Long userId, NotificationTargetType targetName);
}
