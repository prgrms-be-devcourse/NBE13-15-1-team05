package team05.cafe.domain.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlaceOrderRequest(
        @NotNull
        @Size(max = 100)
        String email,

        @NotNull
        @Size(min = 1, max = 200)
        String address,

        @NotNull
        String postcode,

        @NotNull
        @Size(min = 1, max = 10)
        List<Item> items

) {

    public record Item(
            @NotNull
            @Min(value = 1)
            Long menuId,

            @NotNull
            @Min(value = 1)
            @Max(value = 20)
            Integer quantity
    ){
    }
}
