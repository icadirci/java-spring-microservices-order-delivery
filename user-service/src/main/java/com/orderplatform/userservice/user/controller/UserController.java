package com.orderplatform.userservice.user.controller;

import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.userservice.auth.dto.response.UserMeResponse;
import com.orderplatform.userservice.auth.dto.request.UpdateProfileRequest;
import com.orderplatform.userservice.user.dto.request.AddressRequest;
import com.orderplatform.userservice.user.dto.request.ChangePasswordRequest;
import com.orderplatform.userservice.user.dto.response.AddressResponse;
import com.orderplatform.userservice.user.dto.response.DashboardResponse;
import com.orderplatform.userservice.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> me(Authentication authentication) {
        return ApiResponse.ok(userService.getMe(authentication));
    }


//    @GetMapping("/{id}")
//    public ApiResponse<UserResponse> getById(@PathVariable UUID id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(UserNotFoundException::new);
//        return ApiResponse.ok(new UserResponse(
//            user.getId(),
//                user.getEmail(),
//                user.getFullName(),
//                user.isEnabled()
//        ));
//    }

    @PutMapping("/me")
    public ApiResponse<UserMeResponse> updateMe(
            @RequestBody @Valid UpdateProfileRequest request,
            Authentication authentication
    ) {
        return ApiResponse.ok(userService.updateMe(request, authentication));
    }

    // TODO: Implement the endpoints below.
    @GetMapping("/dashboard")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<DashboardResponse> dashboard() {
        return null;
    }

    @PutMapping("/me/password")
    public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request, Authentication authentication) {
        userService.changePassword(request, authentication);
        return ApiResponse.ok(null);
    }

    @PostMapping("/me/addresses")
    public ApiResponse<AddressResponse> createAddress(@RequestBody @Valid AddressRequest request, Authentication authentication) {
        AddressResponse savedAddress = userService.createAddress(request, authentication);
        return ApiResponse.ok(savedAddress);
    }

    @GetMapping("/me/addresses")
    public ApiResponse<List<AddressResponse>> getMyAddresses(Authentication authentication) {
        List<AddressResponse> addresses = userService.getMyAddresses(authentication);
        return ApiResponse.ok(addresses);
    }

    @PutMapping("/me/addresses/{addressId}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable UUID addressId, @RequestBody @Valid AddressRequest request, Authentication authentication) {
        AddressResponse updatedAddress = userService.updateAddress(addressId, request, authentication);
        return ApiResponse.ok(updatedAddress);
    }

    @DeleteMapping("/me/addresses/{addressId}")
    public ApiResponse<Void> deleteAddress(@PathVariable UUID addressId, Authentication authentication) {
        userService.deleteAddress(addressId, authentication);
        return ApiResponse.ok(null);
    }
}
