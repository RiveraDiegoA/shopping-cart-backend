package com.challeng.shopping_cart.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("carts")
public class Cart {
    @Id private String id;
    private String code;
    private String username;
    private List<CartItem> items;
    private String couponCode;
    private String couponName;
    private String couponDescription;
    private double couponDiscountPercent;
    private double subtotal;
    private double discount;
    private double total;
    private double totalUSD;
    private boolean confirmed;
}

