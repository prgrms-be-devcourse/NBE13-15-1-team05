package team05.cafe.domain.menu;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team05.cafe.domain.category.Category;
import team05.cafe.global.util.TextNormalizer;

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

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false)
    private boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id", foreignKey = @ForeignKey(name = "fk_menu_category"))
    public Category category;

    @Builder
    public Menu(String name, int price, String description, Category category) {
        String trimCheckName = TextNormalizer.trimAndCheckLength(name, "name", 1, 100, "메뉴명은 1~100자입니다.");
        String trimDescription = TextNormalizer.trimAndCheckLength(description, "description", 0, 300, "설명은 0~300자입니다.");

        this.name = trimCheckName;
        this.price = price;
        this.description = trimDescription;
        this.category = category;
    }

    public void update(String name, int price, String description, Category category) {

        String trimCheckName = TextNormalizer.trimAndCheckLength(name, "name", 1, 100, "메뉴명은 1~100자입니다.");
        String trimDescription = TextNormalizer.trimAndCheckLength(description, "description", 0, 300, "설명은 0~300자입니다.");

        this.name = trimCheckName;
        this.price = price;
        this.description = trimDescription;
        this.category = category;
    }

    public void changeAvailability(boolean available) {
        this.available = available;
    }

}
