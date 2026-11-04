package team05.cafe.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM category
                WHERE BINARY name = BINARY :name
            )
            """, nativeQuery = true)
    int existsByNameExactly(@Param("name") String name);
}