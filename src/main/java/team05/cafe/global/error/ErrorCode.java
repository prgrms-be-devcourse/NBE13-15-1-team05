package team05.cafe.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "입력값을 확인해 주세요."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C999", "서버 오류가 발생했습니다"),

    // 도메인별
    //카테고리
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "카테고리를 찾을 수 없습니다."),

    CATEGORY_NAME_DUPLICATED(
            HttpStatus.CONFLICT,
            "CATEGORY_NAME_DUPLICATED",
            "같은 이름의 카테고리가 이미 있습니다."
    ),

    CATEGORY_IN_USE(
            HttpStatus.CONFLICT,
            "CATEGORY_IN_USE",
            "메뉴가 등록된 카테고리는 삭제할 수 없습니다."
    ),


    //메뉴
    NOT_FOUND_MENU(HttpStatus.NOT_FOUND, "MENU_NOT_FOUND", "메뉴를 찾을 수 없습니다."),
    MENU_NO_STOCK(HttpStatus.CONFLICT, "MENU_NOT_FOUND", "재고가 없습니다. "),
    MENU_IN_USE(
            HttpStatus.CONFLICT,
            "MENU_IN_USE",
            "주문에 사용된 메뉴는 삭제할 수 없습니다."
    ),

    MENU_UNAVAILABLE(
            HttpStatus.CONFLICT,
            "MENU_UNAVAILABLE",
            "판매 중지된 메뉴가 포함되어 있습니다."
    ),

    //주문
    ORDER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ORDER_NOT_FOUND",
            "주문을 찾을 수 없습니다."
    ),

    ORDER_NOT_CANCELLABLE(
            HttpStatus.CONFLICT,
            "ORDER_NOT_CANCELLABLE",
            "배송 대상일 14시 전의 접수 상태 주문만 취소할 수 있습니다."
    ),

    ADDRESS_MISMATCH(
            HttpStatus.CONFLICT,
            "ADDRESS_MISMATCH",
            "같은 이메일과 배송일의 기존 주문과 배송지가 다릅니다."
    ),



    ZZZZZZZZZZZZZZZZZZZZ(HttpStatus.BAD_REQUEST, "9999", "마지막 에러");

    private final HttpStatus status;
    private final String code;
    private final String message;
}