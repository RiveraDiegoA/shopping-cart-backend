package com.challeng.shopping_cart.infraestructure.persistence;

import com.challeng.shopping_cart.domain.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
    Mono<Category> findByCode(String code);
    Mono<Boolean> existsByName(String name);
}

