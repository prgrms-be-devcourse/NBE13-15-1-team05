package team05.cafe.domain.menu;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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

    @PostMapping
    public ResponseEntity<MenuResponse> create(@RequestBody @Valid  MenuCreateRequest req) {
        MenuResponse saved = menuService.create(req);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()        // 현재 요청 URL: http://host/api/members
                .path("/{id}")               // 뒤에 /{id} 붙임
                .buildAndExpand(saved.id())  // {id} → 5
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<MenuResponse>> findAll() {
        return ResponseEntity.ok(menuReadService.findAll());
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponse> findById(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuReadService.findById(menuId));
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<Long> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable Long menuId,@Valid @RequestBody MenuUpdateRequest req) {
        return ResponseEntity.ok(menuService.update(menuId ,req));
    }


    //200 · 전체 Menu
    @PutMapping("/{menuId}/availability")
    public ResponseEntity<List<MenuResponse>> updateMenuAvailability(@PathVariable Long menuId) {
        menuService.changeAvailability(menuId);
        {
            return ResponseEntity.ok(menuReadService.findAll());
        }
    }

}
