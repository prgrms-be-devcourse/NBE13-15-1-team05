package team05.cafe.order.change;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.OrderStatus;
import team05.cafe.domain.order.change.OrderChangeService;
import team05.cafe.domain.order.dto.OrderResponse;
import team05.cafe.domain.order.repository.OrderItemRepository;
import team05.cafe.domain.order.service.OrderService;
import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 주문 변경(A16 취소, A22 메모) 업무 규칙 테스트.
 * 실제 PC 시계를 바꾸지 않고, 고정된 서울 시각 Clock을 넣어 14:00 경계를 확인한다.
 */
class OrderChangeServiceTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    // 11/3 10:00 접수 → 14시 전이므로 배송일 11/3, 취소 마감 11/3 14:00:00
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 11, 3, 10, 0);

    private final OrderService orderService = mock(OrderService.class);
    private final OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);

    @Test
    @DisplayName("A16: 배송일 13:59:59에는 취소되고 상태·취소 시각만 바뀐다")
    void cancel_justBeforeDeadline() {
        Order order = givenOrder(1L);
        LocalDateTime now = LocalDateTime.of(2026, 11, 3, 13, 59, 59);

        OrderResponse result = serviceAt(now).cancel(1L);

        assertThat(order.getShippingDate()).isEqualTo(LocalDate.of(2026, 11, 3));
        assertThat(result.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(result.cancelledAt()).isEqualTo(now);
        assertThat(result.totalAmount()).isEqualTo(order.getTotalAmount());   // 금액은 그대로
    }

    @Test
    @DisplayName("A16: 배송일 14:00:00 정각부터는 409 ORDER_NOT_CANCELLABLE")
    void cancel_atDeadline() {
        Order order = givenOrder(1L);

        assertThatThrownBy(() -> serviceAt(LocalDateTime.of(2026, 11, 3, 14, 0, 0)).cancel(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ORDER_NOT_CANCELLABLE);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PLACED);   // 실패하면 아무것도 안 바뀜
    }

    @Test
    @DisplayName("A16: 이미 취소된 주문은 마감이 지나도 200, 최초 cancelledAt을 유지한다")
    void cancel_alreadyCancelled() {
        givenOrder(1L);
        LocalDateTime firstCancel = LocalDateTime.of(2026, 11, 3, 11, 0);
        serviceAt(firstCancel).cancel(1L);

        OrderResponse again = serviceAt(LocalDateTime.of(2026, 11, 5, 9, 0)).cancel(1L);   // 마감 이후 재취소

        assertThat(again.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(again.cancelledAt()).isEqualTo(firstCancel);
    }

    @Test
    @DisplayName("A22: 메모는 공백·줄바꿈을 그대로 저장하고, 취소된 주문도 수정할 수 있으며 \"\"는 비우기다")
    void updateMemo_keepsWhitespace() {
        Order order = givenOrder(1L);
        ReflectionTestUtils.setField(order, "status", OrderStatus.CANCELLED);
        OrderChangeService service = serviceAt(CREATED_AT);

        assertThat(service.updateMemo(1L, "  문 앞에\n놓아 주세요  ").memo()).isEqualTo("  문 앞에\n놓아 주세요  ");
        assertThat(service.updateMemo(1L, "").memo()).isEmpty();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);   // 메모만 바뀜
    }

    // ── 도우미 ──

    private Order givenOrder(Long id) {
        Order order = new Order("buyer@example.com", "서울시 실습로 10 101호", "01234", CREATED_AT);
        ReflectionTestUtils.setField(order, "id", id);
        when(orderService.getOrder(id)).thenReturn(order);
        when(orderItemRepository.findByOrderIdOrderByIdAsc(anyLong())).thenReturn(List.of());
        return order;
    }

    /** 지정한 서울 시각에 멈춘 시계를 넣은 서비스 */
    private OrderChangeService serviceAt(LocalDateTime seoulTime) {
        Clock fixed = Clock.fixed(seoulTime.atZone(SEOUL).toInstant(), SEOUL);
        return new OrderChangeService(orderService, orderItemRepository, fixed);
    }
}
