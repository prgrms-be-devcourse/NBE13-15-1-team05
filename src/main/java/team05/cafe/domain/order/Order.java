package team05.cafe.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    public static final LocalTime SHIPPING_CUTOFF = LocalTime.of(14, 0); // 배송 마감 시각

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false, length = 5)
    private String postcode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDate shippingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime cancelledAt;

    @Column(nullable = false)
    private int totalAmount;

    @Column(nullable = false, length = 200)
    private String memo;

    public Order(String email, String address, String postcode, LocalDateTime createdAt) {
        this.email = email;
        this.address = address;
        this.postcode = postcode;
        this.createdAt = createdAt;
        this.shippingDate = calculateShippingDate(this.createdAt);
        this.totalAmount = 0;
        this.status = OrderStatus.PLACED;
        this.cancelledAt = null;
        this.memo = "";
        // 새 주문은 PLACED / cancelledAt=null / memo=""
    }

    // 스태틱이어서 테스트에서 바로 확인 가능하옵니다, 배송 기준에 따라 일자 계산
    public static LocalDate calculateShippingDate(LocalDateTime createdAt) {
        LocalDate date = createdAt.toLocalDate();
        if (createdAt.toLocalTime().isBefore(SHIPPING_CUTOFF)) {
            return date;
        } else {
            return date.plusDays(1);
        }
    }

    public boolean isPlaced() {
        return this.status == OrderStatus.PLACED;
    }

    public void confirmItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("주문 항목이 비어 있습니다.");
        }
        int sum = 0;
        for (OrderItem item : items) {
            if (item.getOrder() != this) {
                throw new IllegalArgumentException("다른 주문의 항목이 포함되어 있습니다.");
            }
            sum += item.getLineAmount();
        }
        this.totalAmount = sum;
    }



}

