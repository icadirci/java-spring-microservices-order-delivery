package com.orderplatform.userservice.user.service;

import com.orderplatform.userservice.auth.dto.request.UpdateProfileRequest;
import com.orderplatform.userservice.auth.dto.response.UserMeResponse;
import com.orderplatform.userservice.exception.AddressNotFoundException;
import com.orderplatform.userservice.exception.InvalidCurrentPasswordException;
import com.orderplatform.userservice.exception.UserNotFoundException;
import com.orderplatform.userservice.user.repository.UserAddressRepository;
import com.orderplatform.userservice.user.repository.UserRepository;
import com.orderplatform.userservice.user.dto.request.AddressRequest;
import com.orderplatform.userservice.user.dto.response.AddressResponse;
import com.orderplatform.userservice.user.dto.request.ChangePasswordRequest;
import com.orderplatform.userservice.user.entity.User;
import com.orderplatform.userservice.user.entity.UserAddress;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAddressRepository userAddressRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserAddressRepository userAddressRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAddressRepository = userAddressRepository;
    }

    @Transactional(readOnly = true)
    public UserMeResponse getMe(Authentication authentication){
        return toMeResponse(getUserByEmail(authentication.getName()));
    }

    @Transactional
    public UserMeResponse updateMe(UpdateProfileRequest request, Authentication authentication){
        User user = getUserByEmail(authentication.getName());
        user.updateProfile(request.fullName().trim(), request.phone(), request.avatarUrl());
        return toMeResponse(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, Authentication authentication){
        User user = getUserByEmail(authentication.getName());
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCurrentPasswordException();
        }
        String newPassword = passwordEncoder.encode(request.newPassword());

        user.changePassword(newPassword);

    }

    @Transactional
    public AddressResponse createAddress(AddressRequest request, Authentication authentication){
        User user = getUserByEmail(authentication.getName());

        UserAddress address = UserAddress.create(
                request.title(),
                request.recipientName(),
                request.phone(),
                request.city(),
                request.district(),
                request.addressLine(),
                request.postalCode(),
                request.isDefault()
        );

        user.addAddress(address);
        userAddressRepository.save(address);

        return AddressResponse.from(address);

    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(Authentication  authentication) {
        User user = getUserByEmail(authentication.getName());
        return user.getAddresses().stream()
                .map(AddressResponse::from)
                .toList();
    }

    @Transactional
    public AddressResponse updateAddress(UUID addressId, AddressRequest request, Authentication authentication){
        User user  = getUserByEmail(authentication.getName());
        UserAddress userAddress = userAddressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(AddressNotFoundException::new);

        userAddress.updateFromRequest(request);
        return AddressResponse.from(userAddress);
    }

    @Transactional
    public void deleteAddress(UUID addressId, Authentication authentication){
        User user  = getUserByEmail(authentication.getName());
        UserAddress userAddress = userAddressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(AddressNotFoundException::new);
        userAddress.softDelete();
    }




    private UserMeResponse toMeResponse(User user){
        return new UserMeResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }


}
