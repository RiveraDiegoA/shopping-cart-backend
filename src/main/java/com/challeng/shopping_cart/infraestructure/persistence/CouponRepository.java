package com.challeng.shopping_cart.infraestructure.persistence;

import com.challeng.shopping_cart.domain.Coupon;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CouponRepository extends ReactiveMongoRepository<Coupon, String> {
    Mono<Boolean> existsByName(String name);
    Mono<Coupon> findByCode(String code);
}

