package team05.cafe.domain.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team05.cafe.domain.statistics.dto.CustomerStatisticsResponse;
import team05.cafe.domain.statistics.dto.MenuStatisticsResponse;
import team05.cafe.domain.statistics.dto.OrderStatisticsResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    // A17: 접수일별 주문 통계
    @GetMapping("/orders")
    public OrderStatisticsResponse orders(@RequestParam(required = false) String date) {
        return statisticsService.getOrderStatistics(DateParams.parse(date, "date"));
    }

    // A24: 접수일별 고객 통계
    @GetMapping("/customers")
    public List<CustomerStatisticsResponse> customers(@RequestParam(required = false) String date) {
        return statisticsService.getCustomerStatistics(DateParams.parse(date, "date"));
    }


    // A18
    @GetMapping("/menus")
    public ResponseEntity<List<MenuStatisticsResponse>> getStatistics(@RequestParam(required = false) String date) {
        return ResponseEntity.ok(statisticsService.getMenuStatistics(DateParams.parse(date, "date")));
    }
}
