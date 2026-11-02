package team05.cafe.domain.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // A03: 카테고리 등록
    @Transactional
    public CategoryResponse create(CategoryCreateRequest request) {
        Category category = new Category(request.name());
        categoryRepository.save(category);
        return CategoryResponse.from(category);
    }

    // A01: 카테고리 전체 조회
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(category -> CategoryResponse.from(category))
                .toList();
    }

    // A02: 카테고리 단건 조회
    @Transactional(readOnly = true)
    public CategoryResponse findOne(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY)
                );

        return CategoryResponse.from(category);
    }

    //A04: 카테고리 수정
    @Transactional
    public CategoryResponse update(Long categoryId, CategoryUpdateRequest request){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));
        category.updateName(request.name());
        return CategoryResponse.from(category);
    }

    //A05: 카테고리 삭제
    @Transactional
    public void delete(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));
        categoryRepository.delete(category);
    }
}