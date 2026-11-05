package team05.cafe.domain.order.change;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team05.cafe.domain.order.dto.OrderResponse;

/**
 * 주문 변경 API. 주문 접수·조회(OrderController)와 같은 주소(/api/v1/orders)를 쓰지만 파일을 나눠 관리한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderChangeController {

    private final OrderChangeService orderChangeService;

    // A16: 주문 취소 (요청 본문 없음) → 200 · 전체 Order
    @PostMapping("/{orderId}/cancel")
    public OrderResponse cancel(@PathVariable @Positive Long orderId) {
        return orderChangeService.cancel(orderId);
    }

    // A22: 주문 메모 수정 → 200 · 전체 Order
    @PutMapping("/{orderId}/memo")
    public OrderResponse updateMemo(@PathVariable @Positive Long orderId,
                                    @RequestBody MemoUpdateRequest request) {
        return orderChangeService.updateMemo(orderId, request.validMemo());
    }
}
