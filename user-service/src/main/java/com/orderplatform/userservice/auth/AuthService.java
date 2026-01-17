package com.orderplatform.userservice.auth;

import com.orderplatform.common.event.UserRegisteredEvent;
import com.orderplatform.common.security.Role;
import com.orderplatform.userservice.auth.dto.AuthResponse;
import com.orderplatform.userservice.auth.dto.LoginRequest;
import com.orderplatform.userservice.auth.dto.RegisterRequest;
import com.orderplatform.userservice.exception.EmailAlreadyExistsException;
import com.orderplatform.userservice.exception.InvalidCredentialsException;
import com.orderplatform.userservice.exception.UserDisabledException;
import com.orderplatform.userservice.security.JwtService;
import com.orderplatform.userservice.user.entity.User;
import com.orderplatform.userservice.user.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RabbitTemplate rabbitTemplate;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        User u = new User();
        u.setEmail(email);
        u.setFullName(req.fullname().trim());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRole(Role.USER);
        u.setEnabled(true);

        User saved = userRepository.save(u);

        String token = jwtService.generateAccessToken(saved.getId(), saved.getEmail(), saved.getRole());

        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID().toString(),
                u.getId(),
                u.getEmail(),
                u.getFullName()
        );

        rabbitTemplate.convertAndSend(
                "user.exchange",
                "user.registered",
                event
        );

        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();

        User u = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!u.isEnabled()) {
            throw new UserDisabledException();
        }

        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateAccessToken(
                u.getId(),
                u.getEmail(),
                u.getRole()
        );

        return new AuthResponse(token, "Bearer");
    }

}
