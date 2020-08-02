package com.example.users.user.repository;

import com.example.users.user.models.Role;
import com.example.users.user.models.Roles;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleRepository extends ReactiveCrudRepository<Role, String> {
    Mono<Role> findByName(Roles name);
}
