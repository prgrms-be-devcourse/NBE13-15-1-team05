package team05.cafe.global.error;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String field;
    private final String reason;

    // 기존 생성자: 일반 오류 (404, 409 등)
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.field = null;
        this.reason = null;
    }

    // 추가 생성자: 특정 필드의 입력 오류 (400)
    public BusinessException(ErrorCode errorCode, String field, String reason) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.field = field;
        this.reason = reason;
    }
}