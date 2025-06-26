package com.challeng.shopping_cart.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    private String productCode;
    private String name;
    private String description;
    private String categoryCode;
    private String categoryName;
    private String urlImage;
    private Double price;
    private int stock;
    private int quantity;
}
