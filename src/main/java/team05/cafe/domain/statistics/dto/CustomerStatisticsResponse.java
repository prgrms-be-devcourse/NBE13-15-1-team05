package team05.cafe.domain.statistics.dto;

import java.time.LocalDate;

/**
 * A24 접수일별 고객 통계 응답 (배열의 원소 하나, 이메일 오름차순)
 * 주문을 모두 취소한 고객도 포함하며, 이때 수량·금액은 0
 */
public record CustomerStatisticsResponse(
        LocalDate date,
        String email,
        long orderCount,
        long placedOrderCount,
        long cancelledOrderCount,
        long totalQuantity,
        long totalAmount
) {
}
