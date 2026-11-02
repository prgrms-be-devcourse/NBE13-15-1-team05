package team05.cafe.domain.statistics;

import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * URL 쿼리의 날짜(date, shippingDate) 검사 도우미.
 * - 형식: YYYY-MM-DD
 * - 범위: 2000-01-01 ~ 2099-12-31
 * - 실제로 존재하는 날짜만 허용 (예: 2026-02-30 은 거절)
 * 조건을 어기면 400 오류를 던진다.
 */
public final class DateParams {

    private static final LocalDate MIN = LocalDate.of(2000, 1, 1);
    private static final LocalDate MAX = LocalDate.of(2099, 12, 31);

    private DateParams() {
    }

    public static LocalDate parse(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw invalid(fieldName);
        }
        LocalDate date;
        try {
            // ISO 형식(YYYY-MM-DD)으로 엄격하게 해석: 없는 날짜는 예외 발생
            date = LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw invalid(fieldName);
        }
        if (date.isBefore(MIN) || date.isAfter(MAX)) {
            throw invalid(fieldName);
        }
        return date;
    }

    // TODO: 공통 예외가 fieldErrors를 지원하면 fieldName을 함께 넘기도록 수정
    private static BusinessException invalid(String fieldName) {
        return new BusinessException(ErrorCode.INVALID_INPUT);
    }
}
