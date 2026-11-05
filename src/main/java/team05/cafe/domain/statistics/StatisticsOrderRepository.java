package team05.cafe.domain.statistics;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import team05.cafe.domain.order.Order;
import team05.cafe.domain.statistics.dto.MenuStatisticsResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 배송·통계 전용 주문 조회 Repository.
 * 주문 담당의 OrderRepository를 수정하지 않도록 조회 메서드만 따로 둔다.
 * (Repository를 상속해서 save/delete 같은 변경 기능은 없다)
 */
public interface StatisticsOrderRepository extends Repository<Order, Long> {

    /** 서울 기준 접수 시각이 start 이상, end 미만인 주문 (취소 포함) */
    List<Order> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(LocalDateTime start, LocalDateTime end);

    @Query("""
        select new team05.cafe.domain.statistics.dto.MenuStatisticsResponse(
            m.id, m.name, m.price, sum(oi.quantity), sum(oi.quantity * m.price)
        )
        from OrderItem oi
        join oi.order o
        join oi.menu m
        where o.createdAt >= :start and o.createdAt < :end
        group by m.id, m.name, m.price
        order by m.id
        """)
    List<MenuStatisticsResponse> getMenuStatistics(LocalDateTime start, LocalDateTime end);
}
