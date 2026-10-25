package com.example.appevemsauth.Service;

import com.example.appevecommon.Models.User.User;
import com.example.appevemsauth.Dto.LoginRequest;
import com.example.appevemsauth.Dto.LoginResponse;
import com.example.appevemsauth.Dto.RegisterRequest;
import com.example.appevemsauth.Repository.IUserRepository;
import com.example.appevemsauth.Service.Exception.InvalidCredentialsException;
import com.example.appevemsauth.Service.User.UserService;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final long expirationMinutes;

    public AuthService(IUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UserService userService,
                       @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
        this.expirationMinutes = expirationMinutes;
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        return userService.register(request);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login para el email {}", request.getEmail());

        // 1. Buscar usuario directamente en el repositorio de la BD
        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> {
                    log.info("Login rechazado: no existe usuario registrado con el email {}", request.getEmail());
                    return new InvalidCredentialsException();
                });

        // 2. Validar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.info("Login rechazado: contraseña incorrecta para el email {}", request.getEmail());
            throw new InvalidCredentialsException();
        }

        // 3. Generar token JWT
        List<String> roles = List.of(user.getRole().getName());
        String token = jwtService.generate(user.getId(), user.getEmail(), roles);
        log.debug("Token emitido para el usuario id={} con roles={}", user.getId(), roles);

        return new LoginResponse(token, "Bearer", expirationMinutes);
    }
}