package team05.cafe.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team05.cafe.domain.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
