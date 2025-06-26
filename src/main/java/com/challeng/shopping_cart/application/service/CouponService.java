package com.challeng.shopping_cart.application.service;

import com.challeng.shopping_cart.domain.Coupon;
import com.challeng.shopping_cart.infraestructure.persistence.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository repository;

    public Flux<Coupon> getAll() {
        return repository.findAll();
    }

    public Mono<Coupon> getById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Cupón no encontrado")));
    }

    public Mono<Coupon> create(Coupon coupon) {
        if (coupon.getDiscountPercent() <= 0 || coupon.getDiscountPercent() > 100) {
            return Mono.error(new IllegalArgumentException("El descuento debe estar entre 0 y 100"));
        }

        return repository.existsByName(coupon.getName())
                .flatMap(exists -> {
                    if (exists) return Mono.error(new RuntimeException("Nombre de cupón duplicado"));
                    return repository.count()
                            .map(count -> String.format("CUP%03d", count + 1))
                            .flatMap(code -> {
                                coupon.setCode(code);
                                coupon.setId(null);
                                return repository.save(coupon);
                            });
                });
    }

    public Mono<Coupon> update(String id, Coupon updateData) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Cupón no encontrado")))
                .flatMap(existing -> {
                    existing.setName(updateData.getName());
                    existing.setDescription(updateData.getDescription());
                    existing.setDiscountPercent(updateData.getDiscountPercent());
                    return repository.save(existing);
                });
    }

    public Mono<Void> delete(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Cupón no encontrado")))
                .flatMap(repository::delete);
    }
}

