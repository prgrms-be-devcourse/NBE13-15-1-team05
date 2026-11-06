package team05.cafe.domain.menu;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team05.cafe.domain.menu.dto.MenuAvailabilityRequest;
import team05.cafe.domain.menu.dto.MenuCreateRequest;
import team05.cafe.domain.menu.dto.MenuResponse;
import team05.cafe.domain.menu.dto.MenuUpdateRequest;
import team05.cafe.domain.menu.service.MenuReadService;
import team05.cafe.domain.menu.service.MenuService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final MenuReadService menuReadService;

    /**
     *
     * @param  req
     * @return MenuResponse
     * 메뉴 생성
     */
    @PostMapping
    public ResponseEntity<MenuResponse> create(@RequestBody @Valid MenuCreateRequest req) {
        MenuResponse saved = menuService.create(req);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()        // 현재 요청 URL: http://host/api/members
                .path("/{id}")               // 뒤에 /{id} 붙임
                .buildAndExpand(saved.id())  // {id} → 5
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    /**
     *
     * @return List<MenuResponse>
     * 전체 메뉴 리턴
     */
    @GetMapping
    public ResponseEntity<List<MenuResponse>> findAll() {
        return ResponseEntity.ok(menuReadService.findAll());
    }

    /**
     *
     * @param menuId
     * @return MenuResponse
     * 특정번호 메뉴 리턴
     */
    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponse> findById(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuReadService.findById(menuId));
    }

    /**
     *
     * @param menuId
     * @return void
     * 특정번호 삭제
     */
    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * @param menuId
     * @param req
     * @return MenuResponse
     * 특정번호 수정
     */
    @PutMapping("/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable Long menuId, @Valid @RequestBody MenuUpdateRequest req) {
        return ResponseEntity.ok(menuService.update(menuId, req));
    }

    /**
     *
     * @param menuId
     * @return MenuResponse
     * 특정번호 상품 판매 유.무 수정
     */
    @PutMapping("/{menuId}/availability")
    public ResponseEntity<MenuResponse> updateMenuAvailability(@PathVariable Long menuId, @RequestBody MenuAvailabilityRequest req) {
        return ResponseEntity.ok(menuService.changeAvailability(menuId,req));
    }

    /**
     *
     * @return  List<MenuResponse>
     * 판매 가능한 상품 리턴
     */
    @GetMapping("/available")
    public List<MenuResponse> findAvailableTrue() {
        return menuReadService.findAvailableTrue();
    }

    /**
     *
     * @param keyword
     * @return List<MenuResponse>
     * 키워드 리턴
     */
    @GetMapping("/search")
    public List<MenuResponse> findByNameContaining(@RequestParam String keyword){
        return menuReadService.findByNameContaining(keyword);
    }

}
