package com.example.users.user.services;

import com.example.users.user.models.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Flux<User> findAll();

    Flux<User> findAll(int page, int size);

    Mono<User> findById(String id);

    Mono<User> findByUsername(String username);

    Mono<User> update(String id, User user);

    Mono<User> save(User user);

    Mono<User> delete(String id);
}
