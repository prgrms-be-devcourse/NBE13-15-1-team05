package team05.cafe.domain.menu.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.domain.category.Category;
import team05.cafe.domain.category.CategoryRepository;
import team05.cafe.domain.menu.Menu;
import team05.cafe.domain.menu.MenuRepository;
import team05.cafe.domain.menu.dto.MenuCreateRequest;
import team05.cafe.domain.menu.dto.MenuResponse;
import team05.cafe.domain.menu.dto.MenuUpdateRequest;
import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;
import team05.cafe.global.redis.CacheNames;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    @CacheEvict(cacheNames = CacheNames.MENUS , key = "'all'")
    @Transactional
    public MenuResponse create(@Valid MenuCreateRequest req) {
        Category category = categoryRepository.findById(req.categoryId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY)
        );

        Menu menu = Menu.builder()
                .name(req.name())
                .price(req.price())
                .description(req.description())
                .category(category)
                .build();
        return MenuResponse.from(menuRepository.save(menu));
    }


    @CacheEvict(cacheNames = {CacheNames.MENUS},allEntries = true)
    @Transactional
    public Long deleteMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MENU)
        );

        menuRepository.delete(menu);
        return menu.getId();
    }

    @CacheEvict(cacheNames = {CacheNames.MENUS},allEntries = true)
    @Transactional
    public MenuResponse update(Long menuId, @Valid MenuUpdateRequest req) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MENU)
        );

        Category category = categoryRepository.findById(req.categoryId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY)
        );


        menu.update(
                req.name(),
                req.price(),
                req.description(),
                category
        );

        return MenuResponse.from(menu);
    }

    @CacheEvict(cacheNames = {CacheNames.MENUS},allEntries = true)
    @Transactional
    public void changeAvailability(@Valid Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MENU)
        );

        menu.changeAvailability();
    }
}
