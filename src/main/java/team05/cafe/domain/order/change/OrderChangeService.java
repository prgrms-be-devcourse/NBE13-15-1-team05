package team05.cafe.domain.order.change;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.dto.OrderResponse;
import team05.cafe.domain.order.repository.OrderItemRepository;
import team05.cafe.domain.order.service.OrderService;
import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * 주문 변경: A16 주문 취소, A22 주문 메모 수정.
 * 주문 조회(getOrder), 응답 변환(OrderResponse), 서울 시계(Clock)는 주문 담당 코드를 재사용한다.
 */
@Service
@RequiredArgsConstructor
public class OrderChangeService {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;
    private final Clock clock;   // TimeConfig의 서울 시각 시계 (테스트에서 시각 고정 가능)

    // ── A16 주문 취소 ──
    @Transactional
    public OrderResponse cancel(Long orderId) {
        Order order = orderService.getOrder(orderId);            // 없으면 ORDER_NOT_FOUND
        LocalDateTime now = LocalDateTime.now(clock);

        // 이미 취소된 주문은 마감과 관계없이 200 + 기존 주문 (cancelledAt 유지)
        if (order.isPlaced() && !order.isCancellableAt(now)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_CANCELLABLE);   // 배송일 14:00:00부터 409
        }
        order.cancel(now);
        return toResponse(order);
    }

    // ── A22 주문 메모 수정 ──
    @Transactional
    public OrderResponse updateMemo(Long orderId, String memo) {
        Order order = orderService.getOrder(orderId);            // 취소된 주문도 메모 수정 가능
        order.updateMemo(memo);
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.from(order, orderItemRepository.findByOrderIdOrderByIdAsc(order.getId()));
    }
}
