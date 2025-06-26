package com.challeng.shopping_cart.infraestructure.controller;

import com.challeng.shopping_cart.application.service.AuthService;
import com.challeng.shopping_cart.domain.Role;
import com.challeng.shopping_cart.domain.User;
import com.challeng.shopping_cart.infraestructure.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public Mono<ResponseEntity<?>> register(@RequestBody User user) {
        user.setRole(Role.ROLE_USER);
        return authService.register(user)
                .map(saved -> ResponseEntity.ok("Usuario registrado"));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody Map<String, String> creds) {
        String username = creds.get("username");
        String password = creds.get("password");

        return authService.authenticate(username, password)
                .map(user -> {
                    String token = jwtUtil.generateToken(user);
                    Map<String, String> response = new HashMap<>();
                    response.put("token", token);
                    response.put("role", user.getRole().name());
                    response.put("username", user.getUsername());
                    response.put("name", user.getName());
                    return ResponseEntity.ok(response);
                });
    }
}
