package team05.cafe.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C999", "서버 오류가 발생했습니다"),

    // 도메인별
    //카테고리
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "C001","카테고리가 없습니다."),


    //메뉴
    NOT_FOUND_MENU(HttpStatus.NOT_FOUND, "C001","메뉴가 없습니다."),


    ZZZZZZZZZZZZZZZZZZZZ(HttpStatus.BAD_REQUEST, "9999", "마지막 에러");

    private final HttpStatus status;
    private final String code;
    private final String message;
}