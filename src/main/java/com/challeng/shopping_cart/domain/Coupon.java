package com.challeng.shopping_cart.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("coupons")
public class Coupon {
    @Id private String id;
    private String code;
    private String name;
    private String description;
    private double discountPercent;
}

