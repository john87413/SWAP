package com.example.demo.service;

import com.example.demo.dto.user.UpdateUserRequest;
import com.example.demo.dto.user.UserDto;
import com.example.demo.dto.user.UserSearchCriteria;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.user.UserRepository;
import com.example.demo.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * 取得所有使用者（分頁）
     */
    public Page<UserDto> getUsers(Pageable pageable) {
        return userRepository.findAllWithRoles(pageable)
                .map(UserMapper::mapToDto);
    }

    /**
     * 進階搜尋使用者（分頁 + 多條件過濾 + 排序）
     */
    public Page<UserDto> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        return userRepository.searchUsers(criteria, pageable);
    }

    /**
     * 根據 ID 取得單一使用者
     */
    public UserDto getUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToDto)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    /**
     * 更新使用者資訊
     */
    @Transactional
    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.setEmail(request.getEmail());
        user.setDisplayName(request.getDisplayName());
        user.setIsActive(request.getIsActive());

        if (request.getRoleIds() != null) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            user.setRoles(new HashSet<>(roles));
        }

        userRepository.save(user);
        return UserMapper.mapToDto(user);
    }

    /**
     * 刪除使用者
     */
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found!");
        }
        userRepository.deleteById(id);
    }
}