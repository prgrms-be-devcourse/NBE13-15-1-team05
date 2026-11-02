package team05.cafe.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;
    private final List<FieldError> errors;

    public static ErrorResponse of(ErrorCode ec) {
        return ErrorResponse.builder().code(ec.getCode()).message(ec.getMessage()).build();
    }

    public static ErrorResponse of(ErrorCode ec, BindingResult br) {
        List<FieldError> errors = br.getFieldErrors().stream()
                .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
                .toList();
        return ErrorResponse.builder().code(ec.getCode()).message(ec.getMessage()).errors(errors).build();
    }

    public record FieldError(String field, String reason) {}
}