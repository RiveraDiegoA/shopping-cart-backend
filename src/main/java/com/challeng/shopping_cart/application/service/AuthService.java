package com.challeng.shopping_cart.application.service;

import com.challeng.shopping_cart.domain.User;
import com.challeng.shopping_cart.infraestructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> register(User user) {
        return userRepository.existsByUsername(user.getUsername())
                .flatMap(exists -> {
                    if (exists) return Mono.error(new RuntimeException("Usuario ya registrado"));
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return userRepository.save(user);
                });
    }

    public Mono<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) return Mono.just(user);
                    else return Mono.error(new RuntimeException("Credenciales inválidas"));
                });
    }
}
