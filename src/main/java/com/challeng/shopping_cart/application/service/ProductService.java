package com.challeng.shopping_cart.application.service;

import com.challeng.shopping_cart.domain.Product;
import com.challeng.shopping_cart.infraestructure.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Flux<Product> getAll() {
        return repository.findAll();
    }

    public Mono<Product> getById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")));
    }

    public Mono<Product> create(Product product) {
        return repository.existsByName(product.getName())
                .flatMap(exists -> {
                    if (exists) return Mono.error(new RuntimeException("Ya existe un producto con ese nombre"));
                    return repository.count()
                            .map(count -> String.format("PROD%03d", count + 1))
                            .flatMap(code -> {
                                product.setCode(code);
                                return repository.save(product);
                            });
                });
    }

    public Mono<Product> update(String id, Product update) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")))
                .flatMap(existing -> {
                    existing.setName(update.getName());
                    existing.setDescription(update.getDescription());
                    existing.setCategoryCode(update.getCategoryCode());
                    existing.setStock(update.getStock());
                    existing.setUrlImage(update.getUrlImage());
                    return repository.save(existing);
                });
    }

    public Mono<Void> delete(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")))
                .flatMap(repository::delete);
    }
}

