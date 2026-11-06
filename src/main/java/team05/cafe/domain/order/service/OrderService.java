package team05.cafe.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.domain.menu.Menu;
import team05.cafe.domain.menu.MenuRepository;
import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.OrderItem;
import team05.cafe.domain.order.OrderStatus;
import team05.cafe.domain.order.dto.OrderResponse;
import team05.cafe.domain.order.dto.OrderSummaryResponse;
import team05.cafe.domain.order.dto.PlaceOrderRequest;
import team05.cafe.domain.order.repository.OrderItemRepository;
import team05.cafe.domain.order.repository.OrderRepository;
import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;
import team05.cafe.global.util.TextNormalizer;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final Clock clock;

    @Transactional
    public OrderResponse place(PlaceOrderRequest request) {
        validateEmail(request.email());
        // 공백제거, 길이검사
        String address = normalizeAddress(request.address());
        String postcode = normalizePostcode(request.postcode());

        // 메뉴 중복 검사
        Set<Long> menuIds = new HashSet<>();
        for (PlaceOrderRequest.Item item : request.items()) {
            if (menuIds.contains(item.menuId())) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "items", "같은 메뉴는 중복될 수 없습니다.");
            }
            menuIds.add(item.menuId());
        }
        return createOrder(request.email(), address, postcode, request.items());
    }

    @Transactional
    public OrderResponse reorder(Long orderId) {
        Order originalOrder = getOrder(orderId);

        List<OrderItem> originalItems = orderItemRepository.findByOrderIdOrderByIdAsc(orderId);
        List<PlaceOrderRequest.Item> items = new ArrayList<>();
        // 리퀘스트의 Item 형식으로 createOrder에 들어가야 하기 때문에 메뉴 id와 수량만 가져옴
        for (OrderItem item : originalItems) {
            items.add(new PlaceOrderRequest.Item(item.getMenuId(), item.getQuantity()));
        }

        return createOrder(originalOrder.getEmail(), originalOrder.getAddress(),
                originalOrder.getPostcode(), items);
    }

    private OrderResponse createOrder(String email, String address, String postcode,
                                      List<PlaceOrderRequest.Item> items) {
        // 받아온 메뉴들이 존재하는지 확인
        List<Menu> menus = new ArrayList<>();
        for (PlaceOrderRequest.Item item : items) {
            Menu menu = menuRepository.findById(item.menuId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MENU));
            menus.add(menu);
        }

        // 판매를 하지 않는 메뉴인지 확인
        for (Menu menu : menus) {
            if (!menu.isAvailable()) {
                throw new BusinessException(ErrorCode.MENU_UNAVAILABLE);
            }
        }

        // 주문 객체 생성
        Order order = new Order(email, address, postcode, LocalDateTime.now(clock));

        // 같은 이메일, 배송일 주문이 PLACED된 주문과 주소, 우편번호가 같은지 확인
        List<Order> sameBatch = orderRepository.findByEmailAndShippingDateAndStatus(
                email, order.getShippingDate(), OrderStatus.PLACED);
        for (Order existing : sameBatch) {
            if (!existing.getEmail().equals(email)) {
                continue;   // DB가 대소문자를 무시해서 섞여 온 다른 고객의 주문은 건너뜀
            }
            if (!existing.hasSameDestination(address, postcode)) {
                throw new BusinessException(ErrorCode.ADDRESS_MISMATCH);
            }
        }

        // 주문 아이템 생성
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            orderItems.add(new OrderItem(order, menus.get(i), items.get(i).quantity()));
        }
        order.confirmItems(orderItems);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        return OrderResponse.from(order, orderItems);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getOrdersByEmail(String email) {
        validateEmail(email);
        List<Order> orders = orderRepository.findByEmailOrderByCreatedAtDescIdDesc(email);

        List<OrderSummaryResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(OrderSummaryResponse.from(order));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = getOrder(orderId);
        List<OrderItem> items = orderItemRepository.findByOrderIdOrderByIdAsc(orderId);
        return OrderResponse.from(order, items);
    }

    private String normalizeAddress(String rawAddress) {
        return TextNormalizer.trimAndCheckLength(rawAddress, "address", 1, 200, "주소는 1~200자여야 합니다.");
    }

    private String normalizePostcode(String rawPostcode) {
        String postcode = TextNormalizer.trimEdges(rawPostcode);
        if (!postcode.matches("^[0-9]{5}$")) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "postcode", "우편번호는 5자리 숫자여야 합니다.");
        }
        return postcode;
    }

    // 이메일 형식 검사 메서드
    private void validateEmail(String email) {
        if (email == null || email.length() > 100
                || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$")) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "email", "이메일 형식을 확인해 주세요.");
        }
    }

    // 주문 조회 메서드
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }
}
