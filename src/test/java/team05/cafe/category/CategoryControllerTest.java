package team05.cafe.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import team05.cafe.domain.category.CategoryController;
import team05.cafe.domain.category.CategoryService;
import team05.cafe.domain.menu.service.MenuReadService;
import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;
import team05.cafe.global.error.GlobalExceptionHandler;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private MenuReadService menuReadService;

    // A02: 존재하지 않는 카테고리 조회 (404)
    @Test
    @DisplayName("A02: 존재하지 않는 카테고리 조회 시 404 반환")
    void findOne_notFound() throws Exception {

        when(categoryService.findOne(999L))
                .thenThrow(new BusinessException(
                        ErrorCode.NOT_FOUND_CATEGORY
                ));

        mockMvc.perform(get("/api/v1/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value("CATEGORY_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("카테고리를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.fieldErrors").isMap())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    // A05: 메뉴가 등록된 카테고리 삭제 (409)
    @Test
    @DisplayName("A05: 사용 중인 카테고리 삭제 시 409 반환")
    void delete_categoryInUse() throws Exception {

        doThrow(new BusinessException(
                ErrorCode.CATEGORY_IN_USE
        )).when(categoryService).delete(1L);

        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code")
                        .value("CATEGORY_IN_USE"))
                .andExpect(jsonPath("$.message")
                        .value("메뉴가 등록된 카테고리는 삭제할 수 없습니다."))
                .andExpect(jsonPath("$.fieldErrors").isMap())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }
}