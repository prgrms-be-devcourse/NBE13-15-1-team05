package team05.cafe.domain.menu.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuUpdateRequest (

        @NotBlank(message = "메뉴명은 필수입니다.")
        String name,
        @NotBlank(message = "메뉴 상세는 필수입니다.")
        String description,

        @NotNull(message = "카테고리 ID는 필수입니다. ")
        Long categoryId,

        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 1, message = "가격은 1보다 작을 수 없습니다.")
        @Max(value = 1000000, message = "가격은 1,000,000보다 클 수 없습니다.")
        Integer price
) {
}
