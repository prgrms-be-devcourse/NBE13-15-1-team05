package team05.cafe.domain.delivery.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * A14 배송 묶음 응답 (배열의 원소 하나, 이메일 오름차순)
 * 이메일과 배송일이 같은 PLACED 주문을 하나로 묶어 보여 준다.
 * 개별 주문은 DB에서 합치지 않고 응답에서만 묶는다.
 */
public record DeliveryBatchResponse(
        LocalDate shippingDate,
        String email,
        String address,
        String postcode,
        List<Long> orderIds,
        int orderCount,
        long totalQuantity,
        long totalAmount,
        List<DeliveryItemResponse> items
) {
}
