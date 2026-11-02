package team05.cafe.domain.delivery.dto;

import team05.cafe.domain.statistics.MergedItem;

/**
 * A14 배송 묶음 안의 항목 하나 (items 배열의 원소)
 */
public record DeliveryItemResponse(
        Long menuId,
        String menuName,
        int unitPrice,
        long quantity,
        long lineAmount
) {
    public static DeliveryItemResponse from(MergedItem item) {
        return new DeliveryItemResponse(
                item.menuId(), item.menuName(), item.unitPrice(),
                item.quantity(), item.amount());
    }
}
