package team05.cafe.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team05.cafe.domain.menu.Menu;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false,foreignKey = @ForeignKey(name = "fk_orderitem_order"))
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orderitem_menu"))
    private Menu menu;

    @Column(nullable = false, length = 100)
    private String menuName;

    @Column(nullable = false)
    private int unitPrice;

    @Column(nullable = false)
    private int quantity; // 주문 수량

    public OrderItem(Order order, Menu menu, int quantity) {
        this.order = order;
        this.menu = menu;
        this.menuName = menu.getName();
        this.unitPrice = menu.getPrice();
        this.quantity = quantity;
    }

    public int getLineAmount() {
        return unitPrice * quantity;
    }

    public Long getMenuId() {
        return menu.getId();
    }

}
