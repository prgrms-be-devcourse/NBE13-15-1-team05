package team05.cafe.domain.statistics;

import org.springframework.data.repository.Repository;
import team05.cafe.domain.order.OrderItem;

import java.util.Collection;
import java.util.List;

/**
 * 배송·통계 전용 주문 항목 조회 Repository.
 * 여러 주문의 항목을 한 번에 가져온다 (주문마다 따로 조회하지 않도록).
 */
public interface StatisticsOrderItemRepository extends Repository<OrderItem, Long> {

    List<OrderItem> findByOrderIdIn(Collection<Long> orderIds);
}
