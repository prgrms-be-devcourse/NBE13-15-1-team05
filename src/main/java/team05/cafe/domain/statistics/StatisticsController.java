package team05.cafe.domain.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team05.cafe.domain.statistics.dto.OrderStatisticsResponse;

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
}
