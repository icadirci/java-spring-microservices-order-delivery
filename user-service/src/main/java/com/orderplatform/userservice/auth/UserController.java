package com.orderplatform.userservice.auth;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.userservice.auth.dto.UserMeResponse;
import com.orderplatform.userservice.auth.dto.UserResponse;
import com.orderplatform.userservice.exception.UserNotFoundException;
import com.orderplatform.userservice.user.UserRepository;
import com.orderplatform.userservice.user.entity.User;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> me(Authentication authentication) {
        String email = authentication.getName();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return ApiResponse.ok(new UserMeResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName()
        ));
    }

    @PreAuthorize("hasRole(T(com.orderplatform.common.security.Role).ADMIN.name())")
    @GetMapping("/dashboard")
    public String dashboard() {
        return "Admin dashboard";
    }


    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException());
        return ApiResponse.ok(new UserResponse(
            user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.isEnabled()
        ));
    }

}
