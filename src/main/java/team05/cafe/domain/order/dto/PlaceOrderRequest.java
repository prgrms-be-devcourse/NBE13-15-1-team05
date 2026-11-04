package team05.cafe.domain.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlaceOrderRequest(
        @NotNull(message = "이메일은 필수입니다.")
        @Size(min = 1, max = 100, message = "이메일은 1~100자여야 합니다.")
        String email,

        @NotNull(message = "주소는 필수입니다.")
        @Size(min = 1, max = 200, message = "주소는 1~200자여야 합니다.")
        String address,

        @NotNull(message = "우편번호는 필수입니다.")
        String postcode,

        @NotNull(message = "주문 항목은 필수입니다.")
        @Size(min = 1, max = 10, message = "주문 항목은 1~10개여야 합니다.")
        List<@NotNull(message = "주문 항목을 확인해 주세요.") @Valid Item> items

) {

    public record Item(
            @NotNull(message = "메뉴 선택은 필수입니다.")
            @Min(value = 1, message = "메뉴는 1개 이상이어야 합니다.")
            Long menuId,

            @NotNull(message = "수량 선택은 필수입니다.")
            @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
            @Max(value = 20, message = "수량은 20개 이하여야 합니다.")
            Integer quantity
    ){
    }
}
