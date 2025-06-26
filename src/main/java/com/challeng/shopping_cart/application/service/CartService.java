package com.challeng.shopping_cart.application.service;

import com.challeng.shopping_cart.domain.Cart;
import com.challeng.shopping_cart.domain.CartItem;
import com.challeng.shopping_cart.infraestructure.persistence.CartRepository;
import com.challeng.shopping_cart.infraestructure.persistence.CouponRepository;
import com.challeng.shopping_cart.infraestructure.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;

    private final double exchangeRate = 3.80;

    public Mono<Cart> getCart(String username) {
        return cartRepository.findByUsernameAndConfirmedFalse(username)
                .switchIfEmpty(createCart(username));
    }

    public Mono<Cart> createCart(String username) {
        return cartRepository.count().map(count -> String.format("CART%03d", count + 1))
                .flatMap(code -> cartRepository.save(
                        Cart.builder()
                                .code(code)
                                .username(username)
                                .items(new ArrayList<>())
                                .subtotal(0)
                                .discount(0)
                                .total(0)
                                .totalUSD(0)
                                .confirmed(false)
                                .build()));
    }

    public Mono<Cart> addProduct(String username, String productCode, int quantity) {
        return getCart(username)
                .flatMap(cart -> {
                    List<CartItem> items = cart.getItems();
                    Optional<CartItem> existing = items.stream()
                            .filter(i -> i.getProductCode().equals(productCode))
                            .findFirst();

                    if (existing.isPresent()) {
                        existing.get().setQuantity(existing.get().getQuantity() + quantity);
                        return Mono.just(cart);
                    }

                    return productRepository.findByCode(productCode)
                            .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado: " + productCode)))
                            .map(product -> {
                                CartItem newItem = new CartItem(productCode, product.getName(), product.getDescription(), product.getCategoryCode(), product.getCategoryName(), product.getUrlImage(), product.getPrice(), product.getStock(), quantity);
                                items.add(newItem);
                                cart.setItems(items);
                                return cart;
                            });
                })
                .flatMap(this::recalculate)
                .flatMap(cartRepository::save);
    }

    public Mono<Cart> removeProduct(String username, String productCode) {
        return getCart(username)
                .flatMap(cart -> {
                    cart.getItems().removeIf(i -> i.getProductCode().equals(productCode));
                    return recalculate(cart);
                }).flatMap(cartRepository::save);
    }

    public Mono<Cart> applyCoupon(String username, String couponCode) {
        return couponRepository.findByCode(couponCode)
                .switchIfEmpty(Mono.error(new RuntimeException("Cupón no válido")))
                .flatMap(coupon -> getCart(username)
                        .map(cart -> {
                            cart.setCouponCode(coupon.getCode());
                            cart.setCouponName(coupon.getName());
                            cart.setCouponDescription(coupon.getDescription());
                            cart.setCouponDiscountPercent(coupon.getDiscountPercent());
                            return cart;
                        }))
                .flatMap(this::recalculate)
                .flatMap(cartRepository::save);
    }

    public Mono<Cart> removeCoupon(String username) {
        return getCart(username)
                .map(cart -> {
                    cart.setCouponCode(null);
                    return cart;
                }).flatMap(this::recalculate)
                .flatMap(cartRepository::save);
    }

    public Mono<Cart> confirm(String username) {
        return getCart(username)
                .flatMap(cart -> {
                    if (cart.getItems().isEmpty()) return Mono.error(new RuntimeException("Carrito vacío"));
                    return Flux.fromIterable(cart.getItems())
                            .flatMap(item -> productRepository.findByCode(item.getProductCode())
                                    .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado: " + item.getProductCode())))
                                    .flatMap(product -> {
                                        if (product.getStock() < item.getQuantity()) {
                                            return Mono.error(new RuntimeException("Stock insuficiente: " + product.getName()));
                                        }
                                        product.setStock(product.getStock() - item.getQuantity());
                                        return productRepository.save(product);
                                    }))
                            .then(Mono.just(cart))
                            .flatMap(this::recalculate)
                            .map(c -> {
                                c.setConfirmed(true);
                                return c;
                            })
                            .flatMap(cartRepository::save);
                });
    }

    private Mono<Cart> recalculate(Cart cart) {
        return Flux.fromIterable(cart.getItems())
                .flatMap(item -> productRepository.findByCode(item.getProductCode())
                        .map(product -> {
                            double itemTotal = product.getPrice() * item.getQuantity();
                            return itemTotal;
                        }))
                .reduce(0.0, Double::sum)
                .flatMap(subtotal -> {
                    cart.setSubtotal(subtotal);

                    if (cart.getCouponCode() != null) {
                        return couponRepository.findByCode(cart.getCouponCode())
                                .map(coupon -> {
                                    double discount = subtotal * (coupon.getDiscountPercent() / 100);
                                    double total = subtotal - discount;
                                    cart.setDiscount(discount);
                                    cart.setTotal(total);
                                    cart.setTotalUSD(total / exchangeRate); // asegúrate que exchangeRate esté definido
                                    return cart;
                                });
                    } else {
                        cart.setDiscount(0);
                        cart.setTotal(subtotal);
                        cart.setTotalUSD(subtotal / exchangeRate);
                        return Mono.just(cart);
                    }
                });
    }

}

