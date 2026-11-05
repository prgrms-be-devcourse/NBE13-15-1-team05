package team05.cafe.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByEmailOrderByCreatedAtDescIdDesc(String email);
    // WHERE email = ? ORDER BY created_at DESC, id DESC

    List<Order> findByEmailAndShippingDateAndStatus(String email, LocalDate shippingDate, OrderStatus status);
    // WHERE email = ? AND shipping_date = ? AND status = ?

    List<Order> findByShippingDateAndStatus(LocalDate shippingDate, OrderStatus status);
    // WHERE shipping_date = ? AND status = ?
}
