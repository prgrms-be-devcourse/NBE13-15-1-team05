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


    public boolean hasSameDestination(String address, String postcode) {
        return this.address.equals(address) && this.postcode.equals(postcode);
    }

    // ── A16 주문 취소 / A22 메모 수정 (주문 변경) ──

    /** 취소 마감: 저장된 배송일의 서울 시각 14:00:00. 정각부터는 취소할 수 없다. */
    public boolean isCancellableAt(LocalDateTime now) {
        return now.isBefore(this.shippingDate.atTime(SHIPPING_CUTOFF));
    }

    /**
     * 주문 취소: 상태와 취소 시각만 바꾸고 주문·항목·금액은 그대로 둔다.
     * 이미 취소된 주문이면 아무것도 바꾸지 않는다 (최초 cancelledAt 유지).
     */
    public void cancel(LocalDateTime now) {
        if (this.status == OrderStatus.CANCELLED) {
            return;
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = now;
    }

    /** 메모 수정: memo만 바꾼다. 공백·줄바꿈은 그대로 저장하고 ""는 메모 비우기. */
    public void updateMemo(String memo) {
        this.memo = memo;
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

