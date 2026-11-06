package team05.cafe.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team05.cafe.domain.order.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ShippingDateTest {

    @Test
    @DisplayName("13:59:59 주문시 당일 배송")
    void beforeCutoff(){
        Order order = orderAt(LocalDateTime.of(2026, 11, 6, 13, 59, 59));
        assertThat(order.getShippingDate()).isEqualTo(LocalDate.of(2026, 11, 6));
    }

    @Test
    @DisplayName("14:00:00 주문시 다음날 배송")
    void atCutoff(){
        Order order = orderAt(LocalDateTime.of(2026, 11, 6, 14, 0, 0));
        assertThat(order.getShippingDate()).isEqualTo(LocalDate.of(2026, 11, 7));
    }

    @Test
    @DisplayName("14:00:00 이후부터 다음날 14:00:00 전 주문은 같은 배송일")
    void sameBatch(){
        Order order1 = orderAt(LocalDateTime.of(2026, 11, 5, 23, 59, 59));
        Order order2 = orderAt(LocalDateTime.of(2026, 11, 6, 0, 0, 0));

        assertThat(order1.getShippingDate()).isEqualTo(LocalDate.of(2026, 11, 6));
        assertThat(order2.getShippingDate()).isEqualTo(LocalDate.of(2026, 11, 6));
    }

    private Order orderAt(LocalDateTime seoulTime) {
        return new Order("name@example.com", "도로명 주소와 상세 주소",
                "01234", seoulTime);
    }
}
