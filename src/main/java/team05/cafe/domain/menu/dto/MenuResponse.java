package team05.cafe.domain.menu.dto;

import team05.cafe.domain.menu.Menu;

public record MenuResponse(Long id, Long categoryId, String name, int price, String description, boolean available) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getCategory().getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getDescription(),
                menu.isAvailable()
        );
    }
}
