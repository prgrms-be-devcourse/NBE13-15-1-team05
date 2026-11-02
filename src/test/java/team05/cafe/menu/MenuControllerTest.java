package team05.cafe.menu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team05.cafe.domain.menu.Menu;
import team05.cafe.domain.menu.MenuController;
import team05.cafe.domain.menu.MenuRepository;
import team05.cafe.domain.menu.dto.MenuCreateRequest;
import team05.cafe.domain.menu.dto.MenuResponse;
import team05.cafe.domain.menu.service.MenuService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@WebMvcTest(MenuController.class)
public class MenuControllerTest {

    @Mock
    MenuRepository menuRepository;
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    MenuService menuService;

    @Test
    @DisplayName("POST /api/v1/menus - 메뉴 등록 성공")
    void 메뉴_등록_성공(){
        MenuCreateRequest req = new MenuCreateRequest("메뉴","설명",1L,10000);
        //카테고리 완료시 다시

        MenuResponse response = menuService.create(req);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.categoryId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("메뉴");
        assertThat(response.description()).isEqualTo("설명");
        assertThat(response.price()).isEqualTo(10000);
        assertThat(response.available()).isEqualTo(true);

        verify(menuRepository).save(any(Menu.class));

    }
}
