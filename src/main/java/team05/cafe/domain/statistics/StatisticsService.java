package team05.cafe.domain.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.OrderItem;
import team05.cafe.domain.order.OrderStatus;
import team05.cafe.domain.statistics.dto.OrderStatisticsDateResponse;
import team05.cafe.domain.statistics.dto.OrderStatisticsResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 접수일별 통계.
 * - date는 배송일이 아니라 서울 기준 "접수일"이다.
 * - 주문 수는 취소 포함, 수량·금액은 PLACED 주문만 합산한다.
 * - 나중에 취소된 주문도 원래 접수일 통계에 들어간다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final StatisticsOrderRepository orderRepository;
    private final StatisticsOrderItemRepository orderItemRepository;

    // ── A17 접수일별 주문 통계 ──
    public OrderStatisticsResponse getOrderStatistics(LocalDate date) {
        List<Order> orders = findOrdersReceivedOn(date);
        if (orders.isEmpty()) {
            return OrderStatisticsResponse.empty(date);   // 주문 없는 날: 404가 아니라 0으로 채운 객체
        }

        long placed = orders.stream().filter(Order::isPlaced).count();
        List<OrderItem> placedItems = findItemsOf(placedOnly(orders));

        return new OrderStatisticsResponse(
                date,
                orders.size(),
                placed,
                orders.size() - placed,
                sumQuantity(placedItems),
                sumAmount(placedItems));
    }

    // ── 공통 도우미 ──

    /**
     * 서울 기준 그날 00:00:00 이상 ~ 다음 날 00:00:00 미만에 접수된 주문 (취소 포함)
     */
    private List<Order> findOrdersReceivedOn(LocalDate date) {
        return orderRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    private List<OrderItem> findItemsOf(List<Order> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }
        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        return orderItemRepository.findByOrderIdIn(orderIds);
    }

    private static List<Order> placedOnly(List<Order> orders) {
        return orders.stream().filter(Order::isPlaced).toList();
    }

    private static long sumQuantity(List<OrderItem> items) {
        return items.stream().mapToLong(OrderItem::getQuantity).sum();
    }

    private static long sumAmount(List<OrderItem> items) {
        return items.stream().mapToLong(OrderItem::getLineAmount).sum();
    }


//    A18 join fetch > 가공
    public OrderStatisticsDateResponse findAllByOrderedDate(LocalDate date) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;

        //sql에서 로우마다 형변환 보다 백단에서 형변환시킴
        LocalDateTime start = targetDate.atStartOfDay();            // 2026-11-03T00:00:00
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();  // 2026-11-04T00:00:00

        List<OrderItem> list = orderItemRepository.findAllByOrderedDate(start ,end);

        long orderCount = list.size();
        long placedCount = list.stream().filter(o -> o.getOrder().getStatus() == OrderStatus.PLACED).count();
        long cancelledOrderCount = list.stream().filter(o -> o.getOrder().getStatus() == OrderStatus.CANCELLED).count();
        long totalQuantity = list.stream().mapToLong(OrderItem::getQuantity).sum();
        long totalAmount = list.stream().mapToLong(OrderItem::getLineAmount).sum();
        return new OrderStatisticsDateResponse(targetDate, orderCount, placedCount, cancelledOrderCount, totalQuantity, totalAmount);


    }

// A18 쿼리  > DTO
    public OrderStatisticsDateResponse findAllByOrderedDateQuery(LocalDate date) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;

        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();
        return orderItemRepository.findAllByOrderedDate2(start, end).orElse(new OrderStatisticsDateResponse(targetDate, 0L, 0L, 0L, 0L, 0L));
    }
}
