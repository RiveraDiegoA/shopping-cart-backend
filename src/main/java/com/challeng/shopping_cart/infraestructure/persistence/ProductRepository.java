package com.challeng.shopping_cart.infraestructure.persistence;

import com.challeng.shopping_cart.domain.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
    Mono<Product> findByCode(String code);
    Mono<Boolean> existsByName(String name);
}

