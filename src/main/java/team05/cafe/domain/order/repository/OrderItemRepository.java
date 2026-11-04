package team05.cafe.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team05.cafe.domain.order.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderIdOrderByIdAsc(Long orderId);
}
