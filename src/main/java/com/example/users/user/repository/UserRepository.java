package com.example.users.user.repository;

import com.example.users.user.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Flux<User> findAllByIdNotNullOrderByIdAsc(final Pageable page);

    Mono<User> findByUsername(String username);
}
