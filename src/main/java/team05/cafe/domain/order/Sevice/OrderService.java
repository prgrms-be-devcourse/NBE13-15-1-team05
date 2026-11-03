package team05.cafe.domain.order.Sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.domain.menu.Menu;
import team05.cafe.domain.menu.MenuRepository;
import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.OrderItem;
import team05.cafe.domain.order.dto.OrderResponse;
import team05.cafe.domain.order.dto.OrderSummaryResponse;
import team05.cafe.domain.order.dto.PlaceOrderRequest;
import team05.cafe.domain.order.repository.OrderItemRepository;
import team05.cafe.domain.order.repository.OrderRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final Clock clock;

    @Transactional
    public OrderResponse place(PlaceOrderRequest request){
        return createOrder(request.email(), request.address(), request.postcode(), request.items());
    }

    private OrderResponse createOrder(String email, String address, String postcode,
                                      List<PlaceOrderRequest.Item> items) {
        Order order = new Order(email, address, postcode, LocalDateTime.now(clock));

        List<OrderItem> orderItems = new ArrayList<>();
        for (PlaceOrderRequest.Item item : items) {
            Menu menu = menuRepository.findById(item.menuId()).orElseThrow();
            orderItems.add(new OrderItem(order, menu, item.quantity()));
        }
        order.confirmItems(orderItems);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        return OrderResponse.from(order, orderItems);
    }

    public List<OrderSummaryResponse> getOrdersByEmail(String email) {
        List<Order> orders = orderRepository.findByEmailOrderByCreatedAtDescIdDesc(email);

        List<OrderSummaryResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(OrderSummaryResponse.from(order));
        }
        return responses;
    }

}
