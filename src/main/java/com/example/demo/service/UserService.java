package com.example.demo.service;

import com.example.demo.dto.user.UpdateUserRequest;
import com.example.demo.dto.user.UserDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Page<UserDto> getUsers(Pageable pageable) {
        return userRepository.findAllWithRoles(pageable)
                .map(this::mapToDto);
    }

    public UserDto getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

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
        return mapToDto(user);
    }

    public void deleteUser(UUID id) {
        if(!userRepository.existsById(id)){
            throw new RuntimeException("User not found!");
        }
        userRepository.deleteById(id);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .isActive(user.getIsActive())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }
}
