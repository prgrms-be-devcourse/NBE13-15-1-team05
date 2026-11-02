package team05.cafe.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.domain.category.CategoryRepository;
import team05.cafe.domain.menu.Menu;
import team05.cafe.domain.menu.MenuRepository;
import team05.cafe.domain.menu.dto.MenuResponse;
import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuReadService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    public List<MenuResponse> findAll() {
        List<Menu> menus = menuRepository.findAll();

        return menus.stream()
                .map(MenuResponse::from)
                .toList();

    }

    public MenuResponse findById(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MENU)
        );

        return MenuResponse.from(menu);
    }
}
