package com.challeng.shopping_cart.infraestructure.persistence;

import com.challeng.shopping_cart.domain.Cart;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CartRepository extends ReactiveMongoRepository<Cart, String> {
    Mono<Cart> findByUsernameAndConfirmedFalse(String username);
}

