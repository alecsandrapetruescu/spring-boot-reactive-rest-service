package com.example.users.user.services;

import com.example.users.user.exceptions.AlreadyExistsException;
import com.example.users.user.models.User;
import com.example.users.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository repository;

    @Autowired
    PasswordEncoder encoder;

    public UserServiceImpl(final UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<User> findAll() {
        return repository.findAll().switchIfEmpty(Flux.empty());
    }

    @Override
    public Flux<User> findAll(int page, int size) {
        return repository.findAllByIdNotNullOrderByIdAsc(PageRequest.of(page, size)).switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<User> findById(final String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<User> findByUsername(final String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Mono<User> update(final String id, final User user) {
        return repository.findById(id)
                .switchIfEmpty(Mono.empty())
                .flatMap(toUpdate -> {
                    toUpdate.setUsername(user.getUsername());
                    return repository.save(toUpdate);
                });
    }

    @Override
    public Mono<User> save(final User user) {
        return findByUsername(user.getUsername())
                .switchIfEmpty(saveT(user).map(e -> {e.setNew(true); return e;}))
                .flatMap(e -> {
                    if (!e.isNew()) {
                        return Mono.error(new AlreadyExistsException("Error: Username " + user.getUsername() + " is already taken!"));
                    }
                    return Mono.just(e);
                });
    }

    private Mono<User> saveT(User user) {
        return repository.save(encodePassword(user));
    }

    private User encodePassword(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return user;
    }

    @Override
    public Mono<User> delete(final String id) {
        return findById(id)
                .switchIfEmpty(Mono.empty())
                .filter(Objects::nonNull)
                .flatMap(toDelete -> repository.delete(toDelete)
                        .then(Mono.just(toDelete)));
    }
}
