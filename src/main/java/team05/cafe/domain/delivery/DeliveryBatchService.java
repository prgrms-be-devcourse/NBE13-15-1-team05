package team05.cafe.domain.delivery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.domain.delivery.dto.DeliveryBatchResponse;
import team05.cafe.domain.delivery.dto.DeliveryItemResponse;
import team05.cafe.domain.order.Order;
import team05.cafe.domain.order.OrderItem;
import team05.cafe.domain.order.OrderStatus;
import team05.cafe.domain.order.repository.OrderItemRepository;
import team05.cafe.domain.order.repository.OrderRepository;
import team05.cafe.domain.statistics.MergedItem;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryBatchService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public List<DeliveryBatchResponse> getDeliveryBatches(LocalDate shippingDate){

        List<Order> orders = orderRepository.findByShippingDateAndStatus(shippingDate, OrderStatus.PLACED);

        if(orders.isEmpty()){
            return List.of();
        }

        Map<String, List<Order>> ordersByEmail = new HashMap<>();
        for(Order order : orders){
            // 해당 이메일의 주문목록, 키는 이메일, 값은 주문목록, 주문 목록 리스트가 들어온다
            List<Order> emailGroup = ordersByEmail.get(order.getEmail());
            if(emailGroup==null){
                emailGroup = new ArrayList<>();
                ordersByEmail.put(order.getEmail(), emailGroup);
            }
            emailGroup.add(order);
        }

        List<Long> orderIds = new ArrayList<>();
        for(Order order : orders){
            orderIds.add(order.getId());
        }
        // 키는 주문 아이디, 값은 주문 아이템 리스트
        Map<Long, List<OrderItem>> itemsByOrderId = new HashMap<>();
        // 주문 아이디들의 해당 주문 아이템들을 전부 다 가져옴
        for(OrderItem item : orderItemRepository.findByOrderIdIn(orderIds)){
            Long orderId = item.getOrder().getId();
            List<OrderItem> itemGroup = itemsByOrderId.get(orderId);
            if(itemGroup==null){
                itemGroup = new ArrayList<>();
                itemsByOrderId.put(orderId, itemGroup);
            }
            itemGroup.add(item);
        }

        // 배송 묶음은 이메일순이기 때문에 오름차순 정렬
        List<String> emails = new ArrayList<>(ordersByEmail.keySet());
        Collections.sort(emails);

        // 정렬 이메일 순서대로 배송 묶음 응답 생성
        List<DeliveryBatchResponse> responses = new ArrayList<>();
        for (String email : emails) {
            List<Order> batchOrders = ordersByEmail.get(email);
            responses.add(toBatch(shippingDate, email, batchOrders, itemsByOrderId));
        }
        return responses;
    }

    // to Batch는 배송 묶음 하나
    private DeliveryBatchResponse toBatch(LocalDate shippingDate, String email,
                                          List<Order> batchOrders, Map<Long, List<OrderItem>> itemsByOrderId) {

        // 배송 묶음 안의 주문 id는 오름차순 정렬
        List<Long> orderIds = new ArrayList<>();
        for (Order order : batchOrders) {
            orderIds.add(order.getId());
        }
        Collections.sort(orderIds);

        // 이 묶음에 속한 주문들의 항목을 모두 모으기
        List<OrderItem> batchItems = new ArrayList<>();
        for (Long orderId : orderIds) {
            List<OrderItem> items = itemsByOrderId.get(orderId);
            if (items != null) {
                batchItems.addAll(items);
            }
        }

        List<MergedItem> merged = mergeItems(batchItems);
        long totalQuantity = 0;
        long totalAmount = 0;
        List<DeliveryItemResponse> itemResponses = new ArrayList<>();
        for (MergedItem item : merged) {
            totalQuantity += item.quantity();
            totalAmount += item.amount();
            itemResponses.add(DeliveryItemResponse.from(item));
        }

        Order first = batchOrders.get(0);

        return new DeliveryBatchResponse(
                shippingDate,
                email,
                first.getAddress(),
                first.getPostcode(),
                orderIds,
                orderIds.size(),
                totalQuantity,
                totalAmount,
                itemResponses);
    }
    private record MergeKey(Long menuId, String menuName, int unitPrice) {
    }

    private List<MergedItem> mergeItems(List<OrderItem> items) {
        Map<MergeKey, Long> quantities = new HashMap<>();
        for (OrderItem item : items) {
            MergeKey key = new MergeKey(item.getMenuId(), item.getMenuName(), item.getUnitPrice());
            quantities.put(key, quantities.getOrDefault(key, 0L) + item.getQuantity());
        }

        List<MergedItem> merged = new ArrayList<>();
        for (Map.Entry<MergeKey, Long> entry : quantities.entrySet()) {
            MergeKey key = entry.getKey();
            long quantity = entry.getValue();
            long amount = (long) key.unitPrice() * quantity;   // 합계는 int를 넘을 수 있어서 long
            merged.add(new MergedItem(key.menuId(), key.menuName(), key.unitPrice(), quantity, amount));
        }

        merged.sort(Comparator.comparing(MergedItem::menuId)
                .thenComparingInt(MergedItem::unitPrice)
                .thenComparing(MergedItem::menuName));
        return merged;
    }
}
