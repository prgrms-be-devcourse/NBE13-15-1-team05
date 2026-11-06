package team05.cafe.global.util;

import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

import java.util.regex.Pattern;

/**
 * 입력 문자열 정리 도우미.
 * 명세: 문자열 양끝의 공백·탭·줄바꿈만 제거하고 중간 공백은 유지한다.
 *      길이는 제거한 뒤에 검사하며, 제한을 넘으면 잘라 저장하지 않고 400으로 거절한다.
 * (이메일·메모처럼 공백을 제거하지 않는 값에는 사용하지 않는다)
 */
public final class TextNormalizer {

    private static final Pattern EDGE_WHITESPACE = Pattern.compile("^[ \\t\\r\\n]+|[ \\t\\r\\n]+$");

    private TextNormalizer() {
    }

    /** 양끝의 공백·탭·줄바꿈만 제거한다. */
    public static String trimEdges(String raw) {
        return EDGE_WHITESPACE.matcher(raw).replaceAll("");
    }

    /**
     * 양끝 공백을 제거한 뒤 길이가 min~max인지 검사한다.
     * 범위를 벗어나면 400 VALIDATION_ERROR + fieldErrors { field: reason }.
     */
    public static String trimAndCheckLength(String raw, String field, int min, int max, String reason) {
        String value = trimEdges(raw);
        if (value.length() < min || value.length() > max) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, field, reason);
        }
        return value;
    }
}
