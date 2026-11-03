package team05.cafe.domain.order.dto;

import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.OrderStatus;
import team05.cafe.global.config.TimeConfig;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record OrderSummaryResponse(
        Long id,

        OffsetDateTime createdAt,

        LocalDate shippingDate,
        int totalAmount,
        OrderStatus status
) {
    public static OrderSummaryResponse from(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getCreatedAt().atZone(TimeConfig.SEOUL).toOffsetDateTime(),
                order.getShippingDate(),
                order.getTotalAmount(),
                order.getStatus());
    }
}