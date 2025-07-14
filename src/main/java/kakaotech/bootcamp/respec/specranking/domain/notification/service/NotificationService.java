package kakaotech.bootcamp.respec.specranking.domain.notification.service;

import static kakaotech.bootcamp.respec.specranking.domain.auth.constant.AuthConstant.LOGIN_REQUIRED_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.notification.constant.NotificationConstant.GET_NOTIFICATION_SUCCESS_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.notification.constant.NotificationConstant.NOT_FOUND_CHAT_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.notification.constant.NotificationConstant.NOT_FOUND_SOCIAL_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.domain.user.constants.UserConstant.USER_NOT_FOUND_MESSAGE_PREFIX;
import static kakaotech.bootcamp.respec.specranking.global.common.type.NotificationTargetType.CHAT;
import static kakaotech.bootcamp.respec.specranking.global.common.type.NotificationTargetType.SOCIAL;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.auth.exception.LoginRequiredException;
import kakaotech.bootcamp.respec.specranking.domain.notification.dto.response.NotificationStatusResponse;
import kakaotech.bootcamp.respec.specranking.domain.notification.dto.response.NotificationStatusResponse.NotificationStatusData;
import kakaotech.bootcamp.respec.specranking.domain.notification.entity.Notification;
import kakaotech.bootcamp.respec.specranking.domain.notification.exception.NotFoundChatNotificationException;
import kakaotech.bootcamp.respec.specranking.domain.notification.exception.NotFoundSocialNotificationException;
import kakaotech.bootcamp.respec.specranking.domain.notification.repository.NotificationRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.exception.UserNotFoundException;
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
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE_PREFIX + receiverId));

        if (notificationRepository.existsByUserIdAndTargetName(receiverId, CHAT)) {
            return;
        }
        Notification notification = new Notification(receiver, CHAT);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public NotificationStatusResponse getFooterNotificationStatus() {
        Long userId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new LoginRequiredException(LOGIN_REQUIRED_MESSAGE));

        boolean hasUnreadChat = notificationRepository.existsByUserIdAndTargetName(userId, CHAT);
        boolean hasUnreadComment = notificationRepository.existsByUserIdAndTargetName(userId, SOCIAL);

        NotificationStatusData data = new NotificationStatusData(hasUnreadChat, hasUnreadComment);

        return new NotificationStatusResponse(true, GET_NOTIFICATION_SUCCESS_MESSAGE, data);
    }

    public void deleteChatNotifications() {
        Long userId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new LoginRequiredException(LOGIN_REQUIRED_MESSAGE));

        List<Notification> chatNotifications = notificationRepository.findByUserIdAndTargetName(userId, CHAT);

        if (chatNotifications.isEmpty()) {
            throw new NotFoundChatNotificationException(NOT_FOUND_CHAT_MESSAGE);
        }

        notificationRepository.deleteAll(chatNotifications);
    }

    public void deleteSocialNotifications() {
        Long userId = UserUtils.getCurrentUserId()
                .orElseThrow(() -> new LoginRequiredException(LOGIN_REQUIRED_MESSAGE));

        List<Notification> socialNotifications = notificationRepository.findByUserIdAndTargetName(userId, SOCIAL);

        if (socialNotifications.isEmpty()) {
            throw new NotFoundSocialNotificationException(NOT_FOUND_SOCIAL_MESSAGE);
        }

        notificationRepository.deleteAll(socialNotifications);
    }
}
