package com.example.users.security.controller;

import com.example.users.security.dto.AuthenticateUser;
import com.example.users.security.dto.Authenticated;
import com.example.users.security.dto.MessageResponse;
import com.example.users.security.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService service;

    @PostMapping("/signin")
    public Mono<ResponseEntity<Authenticated>> authenticate(@Valid @RequestBody AuthenticateUser request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        return service.authenticate(authentication)
                .map(ResponseEntity::ok)
                .doOnError(BadCredentialsException.class, err -> logger.info("Invalid username {} or password!", request.getUsername()))
                .doOnError(UsernameNotFoundException.class, err -> logger.info("Username {} not found!", request.getUsername()))
                .onErrorResume(BadCredentialsException.class, e -> Mono.defer(() -> getMessageResponse(e, HttpStatus.BAD_REQUEST)))
                .onErrorResume(UsernameNotFoundException.class, e -> Mono.defer(() -> getMessageResponse((RuntimeException) e, HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<Void>> register(@Valid @RequestBody AuthenticateUser request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        return service.register(authentication)
                .flatMap(e -> Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                .doOnError(BadCredentialsException.class, err -> logger.info("Username {} already exists!", request.getUsername()))
                .onErrorResume(BadCredentialsException.class, e -> Mono.defer(() -> getMessageResponse(e, HttpStatus.BAD_REQUEST)));
    }

    private Mono getMessageResponse(RuntimeException e, HttpStatus httpStatus) {
        return Mono.just(new ResponseEntity<>(new MessageResponse(e.getMessage()), httpStatus));
    }
}
