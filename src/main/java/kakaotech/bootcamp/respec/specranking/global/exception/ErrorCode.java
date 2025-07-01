package kakaotech.bootcamp.respec.specranking.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    NO_USER_DATA_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 회원 정보가 없습니다."),

    SPEC_NOT_FOUND(HttpStatus.NOT_FOUND, "스펙을 찾을 수 없습니다."),
    SPEC_ACCESS_DENIED(HttpStatus.FORBIDDEN, "스펙에 접근할 권한이 없습니다."),

    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 즐겨찾기를 찾을 수 없습니다."),
    BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 즐겨찾기에 등록된 스펙입니다."),
    SELF_BOOKMARK_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자신의 스펙은 즐겨찾기할 수 없습니다."),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글이거나 해당 스펙에 속하지 않는 댓글입니다."),
    COMMENT_SPEC_MISMATCH(HttpStatus.BAD_REQUEST, "댓글이 해당 스펙에 속하지 않습니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "댓글은 작성자 본인만 수정.삭제가 가능합니다."),
    COMMENT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 댓글입니다."),
    REPLY_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, "대댓글에는 답글을 작성할 수 없습니다. 최상위 댓글에만 답글을 작성해주세요."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 제한을 초과했습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() { return httpStatus; }
    public String getMessage() { return message; }
}
