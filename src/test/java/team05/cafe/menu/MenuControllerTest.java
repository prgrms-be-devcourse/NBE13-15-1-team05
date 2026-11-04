package team05.cafe.menu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team05.cafe.domain.menu.MenuController;
import team05.cafe.domain.menu.dto.MenuCreateRequest;
import team05.cafe.domain.menu.dto.MenuResponse;
import team05.cafe.domain.menu.service.MenuReadService;
import team05.cafe.domain.menu.service.MenuService;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MenuService menuService;

    @MockitoBean
    MenuReadService menuReadService;

    @Test
    @DisplayName("POST /api/v1/menus - 메뉴 등록 성공")
    void 메뉴_등록_성공() throws Exception {
        MenuCreateRequest request = new MenuCreateRequest("메뉴1", "설명1", 1L, 10000);  //리퀘스트생성
        given(menuService.create(any(MenuCreateRequest.class)))  // 주고
                .willReturn(new MenuResponse(1L, 1L, "메뉴1", 10000, "설명1", true)); //받기 이값이 andExpect 에서사용됨

        mockMvc.perform(post("/api/v1/menus") // 형식
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())   //돌아오는값들
                .andExpect(header().string("Location", "http://localhost/api/v1/menus/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.name").value("메뉴1"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.description").value("설명1"))
                .andExpect(jsonPath("$.available").value(true));

        verify(menuService).create(any(MenuCreateRequest.class)); //실제로 던졌는지 까지
    }

    @Test
    @DisplayName("POST /api/v1/menus - 메뉴 등록 실패")
    void 메뉴_등록_실패() throws Exception {
        MenuCreateRequest request = new MenuCreateRequest("메뉴1", "", 1L, 10000);  //리퀘스트생성 설명 x
//        given할필요없음 사용못함
        mockMvc.perform(post("/api/v1/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.description").exists());

        verify(menuService, never()).create(any(MenuCreateRequest.class));
    }
}
