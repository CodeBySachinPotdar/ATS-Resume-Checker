package com.resumematch.service;

import com.resumematch.domain.UserRole;
import com.resumematch.dto.AuthRequest;
import com.resumematch.dto.AuthResponse;
import com.resumematch.dto.RegisterRequest;
import com.resumematch.entity.User;
import com.resumematch.exception.InvalidRequestException;
import com.resumematch.repository.UserRepository;
import com.resumematch.security.JwtTokenProvider;
import com.resumematch.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new InvalidRequestException("An account with email " + request.getEmail() + " already exists.");
        }

        User user = new User(
                request.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName().trim(),
                UserRole.ROLE_USER
        );

        User saved = userRepository.save(user);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );

        String token = tokenProvider.generateToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(saved.getId());

        return new AuthResponse(token, refreshToken, saved.getId(), saved.getEmail(), saved.getFullName(), saved.getRole().name());
    }

    public AuthResponse login(AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow();

        String token = tokenProvider.generateToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        return new AuthResponse(token, refreshToken, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }

    public AuthResponse refresh(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidRequestException("Invalid or expired refresh token.");
        }
        Long userId = tokenProvider.getUserIdFromJWT(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User associated with refresh token not found."));

        UserPrincipal principal = UserPrincipal.create(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        String newToken = tokenProvider.generateToken(auth);
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getId());

        return new AuthResponse(newToken, newRefreshToken, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
