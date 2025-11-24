package com.example.demo.controller;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.user.UpdateUserRequest;
import com.example.demo.dto.user.UserDto;
import com.example.demo.dto.user.UserSearchCriteria;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 取得所有使用者（基本分頁）
     */
    @GetMapping
    public PageResponse<UserDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        Page<UserDto> result = userService.getUsers(pageable);
        return PageResponse.of(result);
    }

    /**
     * 進階搜尋使用者（支援分頁、排序、多條件過濾）
     *
     * @param keyword 關鍵字（搜尋 username、email、displayName）
     * @param isActive 啟用狀態
     * @param roleName 角色名稱
     * @param page 頁碼（從 0 開始）
     * @param size 每頁筆數
     * @param sort 排序欄位，例如：username,asc 或 createdAt,desc
     */
    @GetMapping("/search")
    public PageResponse<UserDto> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username,asc") String[] sort
    ) {
        // 建立搜尋條件
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setIsActive(isActive);
        criteria.setRoleName(roleName);

        // 解析排序參數
        Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        String property = sort[0];

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));

        Page<UserDto> result = userService.searchUsers(criteria, pageable);
        return PageResponse.of(result);
    }

    /**
     * 根據 ID 取得使用者
     */
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    /**
     * 更新使用者
     */
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    /**
     * 刪除使用者
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}