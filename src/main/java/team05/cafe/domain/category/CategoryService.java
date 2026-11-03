package team05.cafe.domain.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team05.cafe.domain.menu.MenuRepository;
import team05.cafe.global.error.BusinessException;
import team05.cafe.global.error.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;

    // A03: 카테고리 등록
    @Transactional
    public CategoryResponse create(CategoryCreateRequest request) {
        String name = normalizeName(request.name());
        validateDuplicateName(name, null);

        Category category = new Category(name);
        categoryRepository.save(category);
        return CategoryResponse.from(category);
    }

    // A01: 카테고리 전체 조회 (ID 오름차순)
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(CategoryResponse::from)
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
    public CategoryResponse update(Long categoryId, CategoryUpdateRequest request) {

        String name = normalizeName(request.name());

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));

        System.out.println("DB 이름: [" + category.getName() + "]");
        System.out.println("요청 이름: [" + name + "]");
        System.out.println("이름이 같은가?: " + category.getName().equals(name));

        // 현재 이름과 다를 때만 중복 검사
        if (!category.getName().equals(name)) {
            validateDuplicateName(name, categoryId);
        }

        category.updateName(name);

        return CategoryResponse.from(category);
    }

    //A05: 카테고리 삭제
    @Transactional
    public void delete(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));

        if (menuRepository.existsByCategoryId(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_IN_USE);
        }

        categoryRepository.delete(category);
    }

    // 양끝 공백 제거 후 길이(1~30자) 검사
    private String normalizeName(String rawName) {
        String name = rawName.replaceAll("^[ \\t\\r\\n]+|[ \\t\\r\\n]+$", "");
        if (name.isEmpty() || name.length() > 30) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "name",
                    "카테고리 이름은 1~30자여야 합니다."
            );
        }
        return name;
    }

    // 다른 카테고리와 이름 중복 검사 (대소문자 구분), excludeId 는 비교 대상에서 제외
    private void validateDuplicateName(String name, Long excludeId) {
        boolean duplicated = categoryRepository.findAll().stream()
                .filter(category -> !category.getId().equals(excludeId)) //자기자신 수정은 200
                .anyMatch(category -> category.getName().equals(name));

        if (duplicated) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_DUPLICATED);
        }
    }
}
