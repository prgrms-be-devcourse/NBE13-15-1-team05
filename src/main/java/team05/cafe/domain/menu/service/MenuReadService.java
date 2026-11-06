package team05.cafe.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.domain.category.CategoryRepository;
import team05.cafe.domain.menu.Menu;
import team05.cafe.domain.menu.MenuRepository;
import team05.cafe.domain.menu.dto.MenuResponse;
import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;
import team05.cafe.global.redis.CacheNames;
import team05.cafe.global.util.TextNormalizer;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuReadService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    @Cacheable(cacheNames = CacheNames.MENUS, key = "'all'")
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

    @Cacheable(cacheNames = CacheNames.MENUS, key = "'availableList'")
    public List<MenuResponse> findAvailableTrue() {
        List<Menu> menus = menuRepository.findByAvailableTrueOrderByIdAsc();

        return menus.stream()
                .map(MenuResponse::from)
                .toList();
    }

    //A15
    public List<MenuResponse> findByCategoryId(Long categoryId) {

        if (!categoryRepository.existsById(categoryId)) { //없으면 404
            throw new BusinessException(ErrorCode.NOT_FOUND_CATEGORY);
        }

        List<Menu> menus = menuRepository.findByCategoryIdOrderByIdAsc(categoryId);

        return menus.stream()
                .map(MenuResponse::from)
                .toList();
    }

    //A20
    public List<MenuResponse> findByNameContaining(String keyword) {

        String name = TextNormalizer.trimAndCheckLength(keyword, "keyword", 1, 100, "검색어는 1~100자여야 합니다.");

        return menuRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .filter(menu -> menu.getName().contains(name))
                .map(MenuResponse::from)
                .toList();
    }
}
