package team05.cafe.domain.menu;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team05.cafe.domain.category.Category;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id")
    public Category category;

    @Builder
    public Menu(String name, int price, String description, Category category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
    }

    public void update(String name, int price, String description, Category category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
    }

    public void changeAvailability() {
        this.available = !this.available;
    }

    //    갯수가 있다면
//    @Column(nullable = false)
//    private int stock;
//
//    void changeStock(int stock) {
//        if (stock > 0) {
//            this.stock = stock;
//        }
//    }
//
//    void checkStock() {
//        if (stock <= 0) {
//            this.available = false;
//        }
//    }
//
//    void decreaseStock(int quantity) {
//        if (this.stock > quantity) {
//            this.stock -= quantity;
//        }
//        throw new BusinessException(ErrorCode.INTERNAL_ERROR);
//    }
}
