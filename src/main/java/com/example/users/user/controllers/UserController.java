package com.example.users.user.controllers;

import com.example.users.security.dto.MessageResponse;
import com.example.users.user.exceptions.AlreadyExistsException;
import com.example.users.user.models.User;
import com.example.users.user.services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.Duration;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private static final int DELAY_PER_ITEM_MS = 100;

    @Autowired
    private final UserServiceImpl service;

    public UserController(final UserServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/with-delay")
    public Flux<User> getAllWithDelayElements() {
        return service.findAll().delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }

    @GetMapping("/with-pagination")
    public Flux<User> getAll(final @RequestParam(name = "page") int page,
                             final @RequestParam(name = "size") int size) {
        return service.findAll(page, size)
                .delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }

    @GetMapping("")
    public Flux<User> getAll() {
        return service.findAll();
    }

    @PostMapping(value = "", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Mono<ResponseEntity<User>> create(@Valid @RequestBody User user) {
        return service.save(user)
                .map(ResponseEntity::ok)
                .doOnError(AlreadyExistsException.class, err -> logger.info("Username {} already exists!", user.getUsername()))
                .onErrorResume(AlreadyExistsException.class,
                        e -> Mono.defer(() -> getMessageResponse(e, HttpStatus.BAD_REQUEST)));
    }

    private Mono getMessageResponse(Throwable e, HttpStatus httpStatus) {
        return Mono.just(new ResponseEntity<>(new MessageResponse(e.getMessage()), httpStatus));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> get(@PathVariable(value = "id") String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> update(@PathVariable(value = "id") String id,
                                                   @Valid @RequestBody User user) {
        return service.update(id, user)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable(value = "id") String id) {
        return service.delete(id)
                .flatMap(deleted -> Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
