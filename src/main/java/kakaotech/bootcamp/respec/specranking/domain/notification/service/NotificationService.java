package kakaotech.bootcamp.respec.specranking.domain.notification.service;

import static kakaotech.bootcamp.respec.specranking.domain.common.type.NotificationTargetType.CHAT;

import kakaotech.bootcamp.respec.specranking.domain.notification.entity.Notification;
import kakaotech.bootcamp.respec.specranking.domain.notification.repository.NotificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public void createChatNotification(Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("User not found: " + receiverId));

        Notification notification = new Notification(receiver, CHAT, receiver.getId());
        notificationRepository.save(notification);
    }

}
