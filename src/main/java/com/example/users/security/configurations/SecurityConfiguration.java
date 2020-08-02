package com.example.users.security.configurations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(
        // securedEnabled = true,
        // jsr250Enabled = true,
        prePostEnabled = true)
public class SecurityConfiguration {

    public static final String ORIGIN = "http://localhost:4200";
    public static final String ALLOWED_HEADER = "*";
    public static final String ALLOWED_METHOD = "*";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> { swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); }))
                .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> { swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN); }))
                .and()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()

                .pathMatchers("/api/auth/**").permitAll()
                .pathMatchers("/api/users/**").permitAll()
//                .pathMatchers("/api/users/**").authenticated()
//                .anyExchange().permitAll();
                .anyExchange().authenticated();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsWebFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin(ORIGIN);
        config.addAllowedHeader(ALLOWED_HEADER);
        config.addAllowedMethod(ALLOWED_METHOD);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

}

