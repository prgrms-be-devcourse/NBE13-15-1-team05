package team05.cafe.domain.statistics;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import team05.cafe.domain.order.OrderItem;
import team05.cafe.domain.statistics.dto.OrderStatisticsDateResponse;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 배송·통계 전용 주문 항목 조회 Repository.
 * 여러 주문의 항목을 한 번에 가져온다 (주문마다 따로 조회하지 않도록).
 */
public interface StatisticsOrderItemRepository extends Repository<OrderItem, Long> {

    List<OrderItem> findByOrderIdIn(Collection<Long> orderIds);

    @Query("""
            select oi from OrderItem oi
            join fetch oi.order
            where oi.order.createdAt >= :start and  oi.order.createdAt  < :end
            """)
    List<OrderItem> findAllByOrderedDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
                select new team05.cafe.domain.statistics.dto.OrderStatisticsDateResponse(
                    o.shippingDate,
                    count(distinct o.id),
                    count(distinct case when o.status = team05.cafe.domain.order.OrderStatus.PLACED then o.id end),
                    count(distinct case when o.status = team05.cafe.domain.order.OrderStatus.CANCELLED then o.id end),
                    sum(oi.quantity),
                    sum(oi.quantity * oi.unitPrice)
                )
                from OrderItem oi
                join oi.order o
                where o.createdAt >= :start and o.createdAt < :end
                group by o.shippingDate
            """)
    Optional<OrderStatisticsDateResponse> findAllByOrderedDate2(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
