package com.challeng.shopping_cart.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("users")
public class User {
    @Id
    private String id;
    private String username; // correo o celular
    private String password;
    private String name;
    private Role role;
}
