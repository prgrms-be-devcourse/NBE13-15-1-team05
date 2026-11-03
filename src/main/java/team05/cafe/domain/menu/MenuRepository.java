package team05.cafe.domain.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    //A19
    List<Menu> findByAvailableTrue();

    //A15
    List<Menu> findByCategoryId(Long categoryId);

    //A20
    List<Menu> findByNameContaining(String keyword);
}