package team05.cafe.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
public class ErrorResponse {

    private final String code;
    private final String message;
    private final Map<String, String> fieldErrors;

    // 일반 오류 (404, 409 등)
    public static ErrorResponse of(ErrorCode ec) {
        return ErrorResponse.builder()
                .code(ec.getCode())
                .message(ec.getMessage())
                .fieldErrors(Map.of())
                .build();
    }
    //특정 필드 입력 오류 & 이유
    public static ErrorResponse of(ErrorCode ec, String field, String reason) {
        return ErrorResponse.builder()
                .code(ec.getCode())
                .message(ec.getMessage())
                .fieldErrors(Map.of(field, reason))
                .build();
    }

    // @Valid 검증 오류 (400)
    public static ErrorResponse of(ErrorCode ec, BindingResult br) {
        Map<String, String> fieldErrors = br.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage() == null
                                ? "입력값이 올바르지 않습니다."
                                : e.getDefaultMessage(),
                        (first, second) -> first
                ));

        return ErrorResponse.builder()
                .code(ec.getCode())
                .message(ec.getMessage())
                .fieldErrors(fieldErrors)
                .build();
    }
}