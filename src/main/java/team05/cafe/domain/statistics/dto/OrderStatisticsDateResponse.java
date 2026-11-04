package team05.cafe.domain.statistics.dto;

import java.time.LocalDate;

public record OrderStatisticsDateResponse(
        LocalDate date,
        Long orderCount,
        Long placedOrderCount,
        Long cancelledOrderCount,
        Long totalQuantity,
        Long totalAmount
) {}
