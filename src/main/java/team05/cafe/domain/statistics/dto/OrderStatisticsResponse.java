package team05.cafe.domain.statistics.dto;

import java.time.LocalDate;

/**
 * A17 접수일별 주문 통계 응답
 * - orderCount: 그날 접수된 전체 주문 수 (취소 포함)
 * - totalQuantity, totalAmount: PLACED 주문만 합산
 */
public record OrderStatisticsResponse(
        LocalDate date,
        long orderCount,
        long placedOrderCount,
        long cancelledOrderCount,
        long totalQuantity,
        long totalAmount
) {
    /**
     * 주문이 하나도 없는 날짜: 404가 아니라 모든 값이 0인 객체를 반환한다.
     */
    public static OrderStatisticsResponse empty(LocalDate date) {
        return new OrderStatisticsResponse(date, 0, 0, 0, 0, 0);
    }


}
