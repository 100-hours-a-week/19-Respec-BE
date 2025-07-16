package kakaotech.bootcamp.respec.specranking.domain.notification.controller;

import static kakaotech.bootcamp.respec.specranking.domain.notification.constant.NotificationConstant.GET_NOTIFICATION_SUCCESS_MESSAGE;

import kakaotech.bootcamp.respec.specranking.domain.notification.dto.response.NotificationStatusResponse;
import kakaotech.bootcamp.respec.specranking.domain.notification.dto.response.NotificationStatusResponse.NotificationStatusData;
import kakaotech.bootcamp.respec.specranking.domain.notification.service.NotificationService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(params = "type=footer")
    public NotificationStatusResponse getNotificationStatus() {
        NotificationStatusData footerNotificationStatus = notificationService.getFooterNotificationStatus();
        return new NotificationStatusResponse(true, GET_NOTIFICATION_SUCCESS_MESSAGE, footerNotificationStatus);
    }

    @DeleteMapping(params = "type=chat")
    public SimpleResponseDto deleteChatNotifications() {
        notificationService.deleteChatNotifications();
        return new SimpleResponseDto(true, "채팅 알림 삭제 성공");
    }

    @DeleteMapping(params = "type=social")
    public SimpleResponseDto deleteSocialNotifications() {
        notificationService.deleteSocialNotifications();
        return new SimpleResponseDto(true, "소셜 알림 삭제 성공");
    }
}
