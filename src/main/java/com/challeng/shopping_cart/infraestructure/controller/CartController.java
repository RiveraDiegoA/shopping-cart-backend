package com.challeng.shopping_cart.infraestructure.controller;

import com.challeng.shopping_cart.application.service.CartService;
import com.challeng.shopping_cart.domain.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Mono<ResponseEntity<Cart>> getCart(@AuthenticationPrincipal Mono<Principal> principal) {
        return principal.map(Principal::getName)
                .flatMap(cartService::getCart)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<?>> addProduct(
            @AuthenticationPrincipal Mono<Principal> principal,
            @RequestBody Map<String, Object> body
    ) {
        String productCode = (String) body.get("productCode");
        int quantity = (int) body.get("quantity");

        return principal.map(Principal::getName)
                .flatMap(username -> cartService.addProduct(username, productCode, quantity))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PostMapping("/remove")
    public Mono<ResponseEntity<?>> removeProduct(
            @AuthenticationPrincipal Mono<Principal> principal,
            @RequestBody Map<String, String> body
    ) {
        String productCode = body.get("productCode");

        return principal.map(Principal::getName)
                .flatMap(username -> cartService.removeProduct(username, productCode))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/apply-coupon")
    public Mono<ResponseEntity<?>> applyCoupon(
            @AuthenticationPrincipal Mono<Principal> principal,
            @RequestBody Map<String, String> body
    ) {
        return principal.map(Principal::getName)
                .flatMap(username -> cartService.applyCoupon(username, body.get("couponCode")))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/remove-coupon")
    public Mono<ResponseEntity<?>> removeCoupon(@AuthenticationPrincipal Mono<Principal> principal) {
        return principal.map(Principal::getName)
                .flatMap(cartService::removeCoupon)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/confirm")
    public Mono<ResponseEntity<?>> confirm(@AuthenticationPrincipal Mono<Principal> principal) {
        return principal.map(Principal::getName)
                .flatMap(cartService::confirm)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }
}

