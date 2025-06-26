package com.challeng.shopping_cart.infraestructure.controller;

import com.challeng.shopping_cart.application.service.ProductService;
import com.challeng.shopping_cart.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping
    public Flux<Product> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getById(@PathVariable String id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    public Mono<ResponseEntity<?>> create(@RequestBody Product product) {
        String name = product.getName();
        if (name == null || name.isBlank()) {
            return Mono.just(ResponseEntity.badRequest().body("Nombre requerido"));
        }

        return service.create(product)
                .<ResponseEntity<?>>map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<?>> update(@PathVariable String id, @RequestBody Product product) {
        return service.update(id, product)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable String id) {
        return service.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}


