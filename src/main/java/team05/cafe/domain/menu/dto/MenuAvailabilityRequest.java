package team05.cafe.domain.menu.dto;

import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

public record MenuAvailabilityRequest(Object available) {

    public boolean validAvailable() {
        if (!(available instanceof Boolean value)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "available", "판매 여부는 true 또는 false여야 합니다.");
        }
        return value;
    }
}