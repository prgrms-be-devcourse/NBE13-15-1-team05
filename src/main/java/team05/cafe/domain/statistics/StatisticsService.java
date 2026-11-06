package team05.cafe.domain.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.OrderItem;
import team05.cafe.domain.statistics.dto.CustomerStatisticsResponse;
import team05.cafe.domain.statistics.dto.MenuStatisticsResponse;
import team05.cafe.domain.statistics.dto.OrderStatisticsResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

    // ── A24 접수일별 고객 통계 ──
    public List<CustomerStatisticsResponse> getCustomerStatistics(LocalDate date) {
        List<Order> orders = findOrdersReceivedOn(date);
        List<Order> placedOrders = placedOnly(orders);

        // 주문 ID → 이메일 (주문 항목이 어느 고객 것인지 찾기 위해)
        Map<Long, String> emailByOrderId = placedOrders.stream()
                .collect(Collectors.toMap(Order::getId, Order::getEmail));

        // 이메일별 누적값 (TreeMap: 이메일 오름차순, 대소문자 구분)
        Map<String, CustomerCounter> byEmail = new TreeMap<>();
        for (Order order : orders) {
            CustomerCounter counter = byEmail.computeIfAbsent(order.getEmail(), k -> new CustomerCounter());
            counter.orderCount++;
            if (order.isPlaced()) {
                counter.placedOrderCount++;
            } else {
                counter.cancelledOrderCount++;
            }
        }
        // 수량·금액은 PLACED 주문의 항목만 더한다
        for (OrderItem item : findItemsOf(placedOrders)) {
            CustomerCounter counter = byEmail.get(emailByOrderId.get(item.getOrder().getId()));
            counter.totalQuantity += item.getQuantity();
            counter.totalAmount += item.getLineAmount();
        }

        // 주문을 모두 취소한 고객도 포함된다 (수량·금액 0)
        List<CustomerStatisticsResponse> result = new ArrayList<>();
        byEmail.forEach((email, c) -> result.add(new CustomerStatisticsResponse(
                date, email, c.orderCount, c.placedOrderCount, c.cancelledOrderCount,
                c.totalQuantity, c.totalAmount)));
        return result;   // 주문이 없는 날은 빈 배열 []
    }

    /**
     * 고객 한 명의 누적값 (A24)
     */
    private static class CustomerCounter {
        long orderCount;
        long placedOrderCount;
        long cancelledOrderCount;
        long totalQuantity;
        long totalAmount;
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

    //    A18
    public List<MenuStatisticsResponse> getMenuStatistics(LocalDate date) {

        //sql에서 로우마다 형변환 보다 백단에서 형변환시킴
        LocalDateTime start = date.atStartOfDay();            // 2026-11-03T00:00:00
        LocalDateTime end = date.plusDays(1).atStartOfDay();  // 2026-11-04T00:00:00

        return orderRepository.getMenuStatistics(start, end);
    }

}
