
package team05.cafe.domain.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // A01: 카테고리 전체 조회
    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    // A02: 카테고리 단건 조회
    @GetMapping("/{categoryId}")
    public CategoryResponse findOne(@PathVariable Long categoryId) {
        return categoryService.findOne(categoryId);
    }

    // A03: 카테고리 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse categoryCreate(@Valid @RequestBody CategoryCreateRequest request) {
        return categoryService.create(request);
    }

    // A04: 카테고리 수정
    @PutMapping("/{categoryId}")
    public CategoryResponse categoryUpdate(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest request) {
        return categoryService.update(categoryId, request);
    }

    // A05: 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void categoryDelete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
    }
}
