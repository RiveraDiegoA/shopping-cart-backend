package com.challeng.shopping_cart.infraestructure.config;

import com.challeng.shopping_cart.infraestructure.persistence.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.getClaims(token);
                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                return userRepository.findByUsername(username)
                        .map(user -> {
                            List<GrantedAuthority> authorities =
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role));
                            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                            return auth;
                        })
                        .doOnNext(SecurityContextHolder.getContext()::setAuthentication)
                        .then(chain.filter(exchange));
            }
        }
        return chain.filter(exchange);
    }
}
