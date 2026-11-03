
package team05.cafe.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외 (404, 409 등)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
        ErrorCode ec = e.getErrorCode();

        log.warn("BusinessException: {}", ec.getCode());

        ErrorResponse response;

        if (e.getField() != null) {
            response = ErrorResponse.of(ec, e.getField(), e.getReason());
        } else {
            response = ErrorResponse.of(ec);
        }

        return ResponseEntity.status(ec.getStatus()).body(response);
    }

    // @Valid 검증 실패 (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValid(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(
                        ErrorCode.INVALID_INPUT,
                        e.getBindingResult()
                ));
    }

    // @Positive 등 Controller 파라미터 검증 실패 (400)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(
            HandlerMethodValidationException e) {

        String field = e.getParameterValidationResults().get(0)
                .getMethodParameter().getParameterName();

        if (field == null) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(ErrorCode.INVALID_INPUT));
        }

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(
                        ErrorCode.INVALID_INPUT,
                        field,
                        "입력값의 범위를 확인해 주세요."
                ));
    }

    // URL 파라미터 자료형 오류 (400)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException e) {

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(
                        ErrorCode.INVALID_INPUT,
                        e.getName(),
                        "올바른 자료형을 입력해 주세요."
                ));
    }

    // 필수 요청 파라미터 누락 (400)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException e) {

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(
                        ErrorCode.INVALID_INPUT,
                        e.getParameterName(),
                        "필수 입력값입니다."
                ));
    }

    // JSON 문법 또는 자료형 오류 (400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException e) {

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT));
    }

    // 예상하지 못한 서버 오류 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        log.error("Unhandled exception", e);

        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR));
    }
}
