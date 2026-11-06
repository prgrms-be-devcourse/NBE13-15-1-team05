package team05.cafe.domain.menu.dto;

import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

public record MenuAvailabilityRequest(Object available) {

    public boolean validAvailable() {
        if (!(available instanceof Boolean value)) {
            throw new BusinessException(ErrorCode.MENU_INVALID_AVAILABLE);
        }
        return value;
    }
}