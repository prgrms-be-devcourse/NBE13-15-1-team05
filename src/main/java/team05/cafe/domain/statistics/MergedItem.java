package team05.cafe.domain.statistics;

/**
 * 주문 항목을 "메뉴 ID + 주문 당시 메뉴명 + 단가" 기준으로 합친 결과.
 * A14 배송 묶음 items와 A18 메뉴 통계가 함께 사용한다.
 */
public record MergedItem(
        Long menuId,
        String menuName,
        int unitPrice,
        long quantity,
        long amount
) {
}
