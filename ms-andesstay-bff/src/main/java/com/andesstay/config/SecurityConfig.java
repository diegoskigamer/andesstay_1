package com.andesstay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final List<String> ROLE_CLAIM_CANDIDATES = List.of("custom:role", "cognito:groups", "roles");

    @Value("${andesstay.security.enabled:false}")
    private boolean securityEnabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        if (!securityEnabled) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/catalog/units").authenticated()
                        .requestMatchers("/api/catalog/**").hasAnyRole("Admin", "admin")
                        .requestMatchers("/api/report/**").hasAnyRole("Admin", "admin")
                        .requestMatchers("/api/audit/**").hasAnyRole("Admin", "admin", "Auditor")
                        .requestMatchers("/api/reservations/**").hasAnyRole("Admin", "admin", "Recepcionista", "Operador", "Huesped", "Cliente")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> scopeAuthorities = scopesConverter.convert(jwt);
            List<GrantedAuthority> result = new ArrayList<>(
                    scopeAuthorities == null ? List.of() : scopeAuthorities);

            for (String claimName : ROLE_CLAIM_CANDIDATES) {
                if (!jwt.hasClaim(claimName)) continue;
                for (String role : parseRoleClaim(jwt.getClaim(claimName))) {
                    result.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }

            return result.stream().distinct().collect(Collectors.toList());
        });

        return converter;
    }

    /**
     * Limpia y desempaca los roles eliminando corchetes, comillas y escapes
     * generados por Cognito al mapear arrays de Azure AD.
     */
    private List<String> parseRoleClaim(Object rawValue) {
        if (rawValue == null) return List.of();

        if (rawValue instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .map(s -> s.replace("\"", "").replace("[", "").replace("]", "").trim())
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toList());
        }

        if (rawValue instanceof String str) {
            String cleaned = str.replaceAll("[\\[\\]\"\\\\]", "").trim();
            if (cleaned.isBlank()) return List.of();

            List<String> values = new ArrayList<>();
            for (String part : cleaned.split(",")) {
                String role = part.trim();
                if (!role.isBlank()) {
                    values.add(role);
                }
            }
            return values;
        }

        return List.of(String.valueOf(rawValue));
    }
}