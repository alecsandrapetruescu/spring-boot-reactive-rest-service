package com.example.users.security.service;

import com.example.users.security.dto.Authenticated;
import com.example.users.user.models.User;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<Authenticated> authenticate(Authentication request);

    Mono<User> register(Authentication request);
}
