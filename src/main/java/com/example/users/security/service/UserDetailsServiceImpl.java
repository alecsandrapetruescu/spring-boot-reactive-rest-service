package com.example.users.security.service;

import com.example.users.security.configurations.TokenProvider;
import com.example.users.security.controller.AuthController;
import com.example.users.security.dto.Authenticated;
import com.example.users.user.models.User;
import com.example.users.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService, AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    UserRepository repository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    PasswordEncoder encoder;

    public UserDetailsServiceImpl() {

    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
       return repository.findByUsername(username)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException("User Not Found")))).map(User::toUserDetails);
    }

    @Override
    public Mono<Authenticated> authenticate(Authentication request) {
        return repository.findByUsername(request.getName())
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Error: User Not Found!")))
                .flatMap(user -> {
                    logger.info("User is: {}", user);

                    if (!encoder.matches(request.getCredentials().toString(), user.getPassword())) {
                        return Mono.error(new BadCredentialsException("Error: Invalid username or password!"));
                    }
                    return Mono.just(new Authenticated(tokenProvider.generateToken(request)));
                });
    }

    @Override
    public Mono<User> register(Authentication request) {
        return save(request);
    }

    public Mono<User> save(final Authentication request) {
        return repository.findByUsername(request.getName())
                .switchIfEmpty(Mono.defer(() -> {
                    User user = new User(request.getPrincipal().toString(), request.getCredentials().toString());
                    user.setPassword(encoder.encode(user.getPassword()));
                    Mono<User> saved = repository.save(user);
                    return saved.map(e -> {e.setNew(true); return e;});
                }))
                .flatMap(user -> {
                        logger.info("User is: {}", user);
                        if (!user.isNew()) {
                            return Mono.error(new BadCredentialsException("Error: Username " + request.getPrincipal() + " is already taken!"));
                        }
                        return Mono.just(user);
                    }
                );
    }
}
