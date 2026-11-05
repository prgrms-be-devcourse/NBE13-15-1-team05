package team05.cafe.domain.order.dto;

import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.OrderItem;
import team05.cafe.domain.order.OrderStatus;
import team05.cafe.global.config.TimeConfig;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public record OrderResponse(
        Long id,
        String email,
        String address,
        String postcode,
        OffsetDateTime createdAt,
        LocalDate shippingDate,
        int totalAmount,
        List<OrderItemResponse> items,
        OrderStatus status,
        OffsetDateTime cancelledAt,
        String memo
) {
    // 주문 항목 엔티티 목록을 응답용 항목으로 목록으로 변환
    public static OrderResponse from(Order order, List<OrderItem> orderItems) {
        List<OrderItemResponse> items = new ArrayList<>();
        for (OrderItem item : orderItems) {
            items.add(OrderItemResponse.from(item));
        }

        return new OrderResponse(
                order.getId(),
                order.getEmail(),
                order.getAddress(),
                order.getPostcode(),
                order.getCreatedAt().atZone(TimeConfig.SEOUL).toOffsetDateTime(),
                order.getShippingDate(),
                order.getTotalAmount(),
                items,
                order.getStatus(),
                order.getCancelledAt().atZone(TimeConfig.SEOUL).toOffsetDateTime(),
                order.getMemo());
    }
}
