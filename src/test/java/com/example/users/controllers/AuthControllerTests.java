package com.example.users.controllers;

import com.example.users.security.dto.AuthenticateUser;
import com.example.users.user.models.User;
import com.example.users.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.timeout;

@AutoConfigureWebTestClient(timeout = "360000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTests {

	public static final String URI = "/api/auth";
	public static final String URI_AUTHENTICATE = URI + "/signin";
	public static final String URI_REGISTER = URI + "/signup";

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	UserService userService;

	@Test
	public void testAuthenticate() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		User user = new User(userName, password);
		user = userService.save(user).block();

		AuthenticateUser token = new AuthenticateUser(userName, password);

		webTestClient.post().uri(URI_AUTHENTICATE)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(token), UsernamePasswordAuthenticationToken.class)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.token").isNotEmpty();
	}

	@Test
	public void testAuthenticate_UsernameNotFound() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		AuthenticateUser token = new AuthenticateUser(userName, password);

		webTestClient.post().uri(URI_AUTHENTICATE)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(token), AuthenticateUser.class)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.jsonPath("$.message").isNotEmpty();
	}


	@Test
	public void testAuthenticate_BadCredentialsException() throws InterruptedException {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		User user = new User(userName, password);
		user = userService.save(user).block();

		timeout(2000L);
		AuthenticateUser token = new AuthenticateUser(userName, "newPass");

		webTestClient.post().uri(URI_AUTHENTICATE)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(token), AuthenticateUser.class)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.message").isNotEmpty();
	}

	@Test
	public void testRegister() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		User user = new User(userName, password);

		AuthenticateUser token = new AuthenticateUser(userName, password);

		webTestClient.post().uri(URI_REGISTER)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(token), AuthenticateUser.class)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void testRegister_BadCredentialsException() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		User user = new User(userName, password);
		user = userService.save(user).block();
		timeout(2000L);

		AuthenticateUser token = new AuthenticateUser(userName, password);

		webTestClient.post().uri(URI_REGISTER)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(token), AuthenticateUser.class)
				.exchange()
				.expectStatus().isBadRequest();
//				.expectBody()
//				.jsonPath("$.message").isNotEmpty();
	}
}
