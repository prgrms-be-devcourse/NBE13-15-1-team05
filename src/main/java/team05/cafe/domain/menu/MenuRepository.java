package team05.cafe.domain.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    // A19: 판매 중 메뉴
    List<Menu> findByAvailableTrueOrderByIdAsc();

    // A15: 카테고리별 메뉴
    List<Menu> findByCategoryIdOrderByIdAsc(Long categoryId);

    // A20: 메뉴명 검색: filter로 대체
//    List<Menu> findByNameContainingOrderByIdAsc(String keyword);

    // A05: 소속 메뉴 존재 여부
    boolean existsByCategoryId(Long categoryId);
}