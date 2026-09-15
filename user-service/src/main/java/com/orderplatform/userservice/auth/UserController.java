package com.orderplatform.userservice.auth;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.userservice.auth.dto.UserMeResponse;
import com.orderplatform.userservice.auth.dto.UserResponse;
import com.orderplatform.userservice.exception.UserNotFoundException;
import com.orderplatform.userservice.user.UserRepository;
import com.orderplatform.userservice.user.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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


    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return ApiResponse.ok(new UserResponse(
            user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.isEnabled()
        ));
    }

    // TODO: Implement profile update.
    @PutMapping("/me")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void updateMe() {
    }

    // TODO: Implement address management.
    @PostMapping("/me/addresses")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void createAddress() {
    }

    @GetMapping("/me/addresses")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void getMyAddresses() {
    }

    @PutMapping("/me/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void updateAddress(@PathVariable UUID addressId) {
    }

    @DeleteMapping("/me/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void deleteAddress(@PathVariable UUID addressId) {
    }

}
