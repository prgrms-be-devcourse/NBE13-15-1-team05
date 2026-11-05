package team05.cafe.domain.order.change;

import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

/**
 * A22 주문 메모 수정 요청 본문: { "memo": "..." }
 *
 * memo를 String이 아니라 Object로 받는 이유:
 * String으로 받으면 { "memo": 123 } 같은 숫자도 JSON 변환기가 "123"으로 바꿔서 받아 버린다.
 * 명세서는 문자열이 아닌 값(숫자·null·누락)을 400으로 거절하라고 하므로, 직접 확인한다.
 */
public record MemoUpdateRequest(Object memo) {

    private static final int MAX_LENGTH = 200;

    /** 검사를 통과한 메모를 돌려준다. 앞뒤 공백을 제거하지 않는다 (공백·줄바꿈 보존). */
    public String validMemo() {
        if (!(memo instanceof String text)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "memo", "메모는 문자열이어야 합니다.");
        }
        if (text.length() > MAX_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "memo", "메모는 0~200자여야 합니다.");
        }
        return text;
    }
}
