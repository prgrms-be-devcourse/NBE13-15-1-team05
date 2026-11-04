package team05.cafe.domain.order.dto;

import team05.cafe.domain.order.OrderItem;

public record OrderItemResponse(
        Long menuId,
        String menuName,
        int unitPrice,
        int quantity,
        int lineAmount
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getMenuId(),
                item.getMenuName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineAmount());
    }
}
