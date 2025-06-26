package com.challeng.shopping_cart.infraestructure.controller;

import com.challeng.shopping_cart.application.service.CouponService;
import com.challeng.shopping_cart.domain.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService service;

    @GetMapping
    public Flux<Coupon> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Coupon>> getById(@PathVariable String id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    public Mono<ResponseEntity<?>> create(@RequestBody Coupon coupon) {
        return service.create(coupon)
                .<ResponseEntity<?>>map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<?>> update(@PathVariable String id, @RequestBody Coupon coupon) {
        return service.update(id, coupon)
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

