package kakaotech.bootcamp.respec.specranking.domain.user.util;

/**
 * 현재 사용자 정보를 가져오는 유틸리티 클래스
 */
public class UserUtils {
    
    /**
     * 현재 로그인한 사용자의 ID를 반환
     * 실제 인증이 구현되기 전까지는 고정 값 반환
     */
    public static Long getCurrentUserId() {
        // TODO: 인증 기능이 구현되면 SecurityContext에서 사용자 정보 가져오도록 수정
        return 1L; // 테스트용 고정 값
    }
}
