package com.example.demo.repository.user;

import com.example.demo.dto.user.UserDto;
import com.example.demo.dto.user.UserSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<UserDto> searchUsers(UserSearchCriteria criteria, Pageable pageable);
}
