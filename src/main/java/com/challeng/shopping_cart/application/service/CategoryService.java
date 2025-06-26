package com.challeng.shopping_cart.application.service;

import com.challeng.shopping_cart.domain.Category;
import com.challeng.shopping_cart.infraestructure.persistence.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public Flux<Category> getAll() {
        return repository.findAll();
    }

    public Mono<Category> getById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Categoría no encontrada")));
    }

    public Mono<Category> create(String name) {
        return repository.existsByName(name)
                .flatMap(exists -> {
                    if (exists) return Mono.error(new RuntimeException("Ya existe una categoría con ese nombre"));

                    return repository.count()
                            .map(count -> String.format("CAT%03d", count + 1))
                            .flatMap(code -> repository.save(new Category(null, code, name)));
                });
    }

    public Mono<Category> update(String id, String newName) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Categoría no encontrada")))
                .flatMap(existing -> {
                    existing.setName(newName);
                    return repository.save(existing);
                });
    }

    public Mono<Void> delete(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Categoría no encontrada")))
                .flatMap(repository::delete);
    }
}

