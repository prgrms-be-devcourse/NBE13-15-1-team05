package team05.cafe.domain.delivery;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team05.cafe.domain.delivery.dto.DeliveryBatchResponse;
import team05.cafe.domain.statistics.DateParams;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery-batches")
@RequiredArgsConstructor
public class DeliveryBatchController {
    private final DeliveryBatchService deliveryBatchService;

    // A14
    @GetMapping
    public List<DeliveryBatchResponse> getDeliveryBatches(@RequestParam(required = false) String shippingDate) {
        return deliveryBatchService.getDeliveryBatches(DateParams.parse(shippingDate, "shippingDate"));
    }

}
