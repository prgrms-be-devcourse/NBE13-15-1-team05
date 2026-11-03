package team05.cafe.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import team05.cafe.domain.menu.Menu;
import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.OrderItem;
import team05.cafe.domain.order.OrderStatus;
import team05.cafe.domain.statistics.StatisticsOrderItemRepository;
import team05.cafe.domain.statistics.StatisticsOrderRepository;
import team05.cafe.domain.statistics.StatisticsService;
import team05.cafe.domain.statistics.dto.OrderStatisticsResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 통계 업무 규칙 단위 테스트 (DB 없이 Repository를 모의 객체로 대체)
 */
class StatisticsServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 11, 3);

    private StatisticsOrderRepository orderRepository;
    private StatisticsOrderItemRepository orderItemRepository;
    private StatisticsService statisticsService;

    private Menu nariño;
    private Menu brazil;

    @BeforeEach
    void setUp() {
        orderRepository = mock(StatisticsOrderRepository.class);
        orderItemRepository = mock(StatisticsOrderItemRepository.class);
        statisticsService = new StatisticsService(orderRepository, orderItemRepository);
        nariño = menu(1L, "Columbia Nariñó", 5000);
        brazil = menu(2L, "Brazil Serra Do Caparaó", 7000);
    }

    @Test
    @DisplayName("A17: 전체 주문 수는 취소 포함, 수량·금액은 PLACED 주문만 합산한다")
    void orderStatistics_excludesCancelledFromQuantityAndAmount() {
        Order placed = order(1L, "buyer@example.com", DAY.atTime(10, 0), OrderStatus.PLACED);
        Order cancelled = order(2L, "buyer@example.com", DAY.atTime(11, 0), OrderStatus.CANCELLED);
        givenOrdersOn(DAY, placed, cancelled);
        givenItems(item(placed, nariño, 3), item(placed, brazil, 1));

        OrderStatisticsResponse result = statisticsService.getOrderStatistics(DAY);

        assertThat(result.orderCount()).isEqualTo(2);
        assertThat(result.placedOrderCount()).isEqualTo(1);
        assertThat(result.cancelledOrderCount()).isEqualTo(1);
        assertThat(result.totalQuantity()).isEqualTo(4);
        assertThat(result.totalAmount()).isEqualTo(22000);
    }

    @Test
    @DisplayName("A17: 주문이 없는 날짜는 모든 값이 0인 객체를 반환한다")
    void orderStatistics_emptyDay() {
        givenOrdersOn(DAY);

        OrderStatisticsResponse result = statisticsService.getOrderStatistics(DAY);

        assertThat(result).isEqualTo(OrderStatisticsResponse.empty(DAY));
    }




    // ── 테스트 데이터 도우미 ──

    private void givenOrdersOn(LocalDate date, Order... orders) {
        when(orderRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(eq(date.atStartOfDay()), eq(date.plusDays(1).atStartOfDay())))
                .thenReturn(List.of(orders));
    }

    @SuppressWarnings("unchecked")
    private void givenItems(OrderItem... items) {
        when(orderItemRepository.findByOrderIdIn(any(Collection.class))).thenAnswer(inv -> {
            Collection<Long> ids = inv.getArgument(0);
            return List.of(items).stream().filter(i -> ids.contains(i.getOrder().getId())).toList();
        });
    }

    private static Menu menu(Long id, String name, int price) {
        Menu menu = Menu.builder().name(name).price(price).description("").build();
        ReflectionTestUtils.setField(menu, "id", id);
        return menu;
    }

    private static Order order(Long id, String email, LocalDateTime createdAt, OrderStatus status) {
        Order order = new Order(email, "서울시 실습로 10 101호", "01234", createdAt);
        ReflectionTestUtils.setField(order, "id", id);
        ReflectionTestUtils.setField(order, "status", status);
        return order;
    }

    private static OrderItem item(Order order, Menu menu, int quantity) {
        return new OrderItem(order, menu, quantity);
    }
}
