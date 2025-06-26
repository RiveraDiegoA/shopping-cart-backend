package com.challeng.shopping_cart.infraestructure.config.seeder;

import com.challeng.shopping_cart.domain.*;
import com.challeng.shopping_cart.infraestructure.persistence.CategoryRepository;
import com.challeng.shopping_cart.infraestructure.persistence.CouponRepository;
import com.challeng.shopping_cart.infraestructure.persistence.ProductRepository;
import com.challeng.shopping_cart.infraestructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedAdminUser();
        seedCategories();
        seedProducts();
        seedCoupons();
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

    private void seedCategories() {
        categoryRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(ignored -> Flux.just(
                        new Category(null, "CAT001", "Bebidas Carbonatadas"),
                        new Category(null, "CAT002", "Aguas"),
                        new Category(null, "CAT003", "Néctares / Jugos")
                ).flatMap(categoryRepository::save))
                .doOnNext(c -> System.out.println("Categoría creada: " + c.getName()))
                .subscribe();
    }

    private void seedProducts() {
        productRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(ignored -> Flux.just(
                        Product.builder()
                                .code("PROD001")
                                .name("Big Cola 1.5L")
                                .description("Refresco sabor cola, también en versiones naranja, piña y manzana.")
                                .categoryCode("CAT001")
                                .categoryName("Bebidas Carbonatadas")
                                .urlImage("https://example.com/bigcola.jpg")
                                .price(12.0)
                                .stock(20)
                                .build(),
                        Product.builder()
                                .code("PROD002")
                                .name("Agua Cielo")
                                .description("Agua purificada y gasificada disponible en PET y vidrio; presente en más de 10 países.")
                                .categoryCode("CAT002")
                                .categoryName("Aguas")
                                .urlImage("https://example.com/oreo.jpg")
                                .price(2.5)
                                .stock(50)
                                .build()
                ).flatMap(productRepository::save))
                .doOnNext(p -> System.out.println("Producto creado: " + p.getName()))
                .subscribe();
    }

    private void seedCoupons() {
        couponRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(ignored -> Flux.just(
                        new Coupon(null, "CUP10", "10% OFF", "Cupón de prueba", 10),
                        new Coupon(null, "CUP20", "20% OFF", "Cupón especial", 20)
                ).flatMap(couponRepository::save))
                .doOnNext(c -> System.out.println("Cupón creado: " + c.getCode()))
                .subscribe();
    }
}
