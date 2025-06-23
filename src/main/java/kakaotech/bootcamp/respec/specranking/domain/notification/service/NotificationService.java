package kakaotech.bootcamp.respec.specranking.domain.notification.service;

import static kakaotech.bootcamp.respec.specranking.global.common.type.NotificationTargetType.CHAT;
import static kakaotech.bootcamp.respec.specranking.global.common.type.NotificationTargetType.SOCIAL;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.notification.dto.response.NotificationStatusResponse;
import kakaotech.bootcamp.respec.specranking.domain.notification.dto.response.NotificationStatusResponse.NotificationStatusData;
import kakaotech.bootcamp.respec.specranking.domain.notification.entity.Notification;
import kakaotech.bootcamp.respec.specranking.domain.notification.repository.NotificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public void createChatNotificationIfNotExists(Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("User not found: " + receiverId));

        if (notificationRepository.existsByUserIdAndTargetName(receiverId, CHAT)) {
            return;
        }
        Notification notification = new Notification(receiver, CHAT);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public NotificationStatusResponse getFooterNotificationStatus() {
        Long userId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        boolean hasUnreadChat = notificationRepository.existsByUserIdAndTargetName(userId, CHAT);
        boolean hasUnreadComment = notificationRepository.existsByUserIdAndTargetName(userId, SOCIAL);

        NotificationStatusData data = new NotificationStatusData(hasUnreadChat, hasUnreadComment);

        return new NotificationStatusResponse(true, "알림 상태 조회 성공", data);
    }

    public void deleteChatNotifications() {
        Long userId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        List<Notification> chatNotifications = notificationRepository.findByUserIdAndTargetName(userId, CHAT);

        if (chatNotifications.isEmpty()) {
            throw new IllegalArgumentException("삭제할 채팅 알림이 없습니다.");
        }

        notificationRepository.deleteAll(chatNotifications);
    }

    public void deleteSocialNotifications() {
        Long userId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        List<Notification> socialNotifications = notificationRepository.findByUserIdAndTargetName(userId, SOCIAL);

        if (socialNotifications.isEmpty()) {
            throw new IllegalArgumentException("삭제할 소셜 알림이 없습니다.");
        }

        notificationRepository.deleteAll(socialNotifications);
    }
}
