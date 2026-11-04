package team05.cafe.statistics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team05.cafe.domain.statistics.StatisticsController;
import team05.cafe.domain.statistics.StatisticsService;
import team05.cafe.domain.statistics.dto.OrderStatisticsResponse;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    StatisticsService statisticsService;

    @Test
    @DisplayName("A17 정상: 200과 OrderStatistics 필수 필드를 반환한다")
    void orderStatistics_ok() throws Exception {
        LocalDate day = LocalDate.of(2026, 11, 3);
        when(statisticsService.getOrderStatistics(day))
                .thenReturn(new OrderStatisticsResponse(day, 2, 2, 0, 4, 22000));

        mockMvc.perform(get("/api/v1/statistics/orders").param("date", "2026-11-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-11-03"))
                .andExpect(jsonPath("$.orderCount").value(2))
                .andExpect(jsonPath("$.placedOrderCount").value(2))
                .andExpect(jsonPath("$.cancelledOrderCount").value(0))
                .andExpect(jsonPath("$.totalQuantity").value(4))
                .andExpect(jsonPath("$.totalAmount").value(22000));
    }

    @Test
    @DisplayName("A17 입력 오류: 존재하지 않는 날짜(2026-02-30)는 400 VALIDATION_ERROR와 fieldErrors.date를 반환한다")
    void orderStatistics_invalidDate() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/orders").param("date", "2026-02-30"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.fieldErrors.date").exists());
    }
}
