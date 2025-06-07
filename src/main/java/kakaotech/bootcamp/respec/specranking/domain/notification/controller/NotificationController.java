package kakaotech.bootcamp.respec.specranking.domain.notification.controller;

import kakaotech.bootcamp.respec.specranking.domain.notification.dto.response.NotificationStatusResponse;
import kakaotech.bootcamp.respec.specranking.domain.notification.service.NotificationService;
import kakaotech.bootcamp.respec.specranking.global.dto.SimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        return notificationService.getFooterNotificationStatus();
    }

    @DeleteMapping(params = "type=chat")
    public SimpleResponseDto deleteChatNotifications() {
        notificationService.deleteChatNotifications();
        return new SimpleResponseDto(true, "채팅 알림 삭제 성공");
    }

    @DeleteMapping(params = "type=social")
    public ResponseEntity<SimpleResponseDto> deleteSocialNotifications() {
        notificationService.deleteSocialNotifications();
        return ResponseEntity.ok(new SimpleResponseDto(true, "소셜 알림 삭제 성공"));
    }
}
