package com.example.appevemsauth.Controller;

import com.example.appevecommon.Service.Utilities.Responses.Response;
import com.example.appevecommon.Service.Utilities.Responses.ResponseFactory;
import com.example.appevemsauth.Dto.LoginRequest;
import com.example.appevemsauth.Dto.LoginResponse;
import com.example.appevemsauth.Dto.RegisterRequest;
import com.example.appevemsauth.Service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.UserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticación y emisión de tokens")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login");
        return ResponseFactory.ok("Autenticación exitosa", authService.login(request));
    }

    @PostMapping("/register")
    public Response<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register para el email {}", request.getEmail());
        return ResponseFactory.resourceCreated(authService.register(request));
    }
}
