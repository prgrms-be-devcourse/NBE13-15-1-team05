package team05.cafe.domain.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
        @NotBlank String name
) {
}
