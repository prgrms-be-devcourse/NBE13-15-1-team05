package team05.cafe.domain.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team05.cafe.domain.order.dto.OrderResponse;
import team05.cafe.domain.order.dto.OrderSummaryResponse;
import team05.cafe.domain.order.dto.PlaceOrderRequest;
import team05.cafe.domain.order.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // A11 주문 접수
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.place(request));
    }

    // A23 재주문
    @PostMapping("/{orderId}/reorder")
    public ResponseEntity<OrderResponse> reorder(@PathVariable @Positive Long orderId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.reorder(orderId));
    }

    // A12 이메일별 주문 조회
    @GetMapping
    public List<OrderSummaryResponse> getOrdersByEmail(@RequestParam(required = false) String email) {

        return orderService.getOrdersByEmail(email);
    }

    // A13 주문 상세 조회
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable @Positive Long orderId) {
        return orderService.getOrderDetail(orderId);
    }

}
