package team05.cafe.domain.statistics.dto;

import team05.cafe.domain.statistics.MergedItem;

/**
 * A18 접수일별 메뉴 통계 응답 (배열의 원소 하나)
 * 메뉴 ID + 주문 당시 메뉴명 + 단가가 같은 항목끼리 합산한 결과
 */
public record MenuStatisticsResponse(
        Long menuId,
        String menuName,
        int unitPrice,
        long totalQuantity,
        long totalAmount
) {
    public static MenuStatisticsResponse from(MergedItem item) {
        return new MenuStatisticsResponse(
                item.menuId(), item.menuName(), item.unitPrice(),
                item.quantity(), item.amount());
    }
}
