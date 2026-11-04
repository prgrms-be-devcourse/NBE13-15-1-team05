package team05.cafe.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team05.cafe.domain.order.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByEmailOrderByCreatedAtDescIdDesc(String email);
}
