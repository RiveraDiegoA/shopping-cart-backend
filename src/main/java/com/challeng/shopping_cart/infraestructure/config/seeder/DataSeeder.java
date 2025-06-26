package com.challeng.shopping_cart.infraestructure.config.seeder;

import com.challeng.shopping_cart.domain.Role;
import com.challeng.shopping_cart.domain.User;
import com.challeng.shopping_cart.infraestructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedAdminUser();
    }

    private void seedAdminUser() {
        userRepository.findByUsername("admin@shop.com")
                .switchIfEmpty(
                        userRepository.save(User.builder()
                                .name("Administrador")
                                .username("admin@shop.com")
                                .password(passwordEncoder.encode("admin123"))
                                .role(Role.ROLE_ADMIN)
                                .build()
                        ).doOnSuccess(u -> System.out.println("Admin creado"))
                ).subscribe();
    }
}
