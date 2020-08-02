package com.example.users.controllers;

import com.example.users.user.models.User;
import com.example.users.user.services.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@AutoConfigureWebTestClient(timeout = "360000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTests {

	public static final String URI = "/api/users";
	public static final String URI_WITH_ID = URI + "/{id}";
	public static final String PATH_VARIABLE_ID = "id";

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private UserService userService;

	private User getUser() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		return new User(userName, password);
	}

	private User getUser(long timestamp) {
		String userName = "username_" + timestamp;
		String password = "password";
		return new User(userName, password);
	}

	@Test
	public void testCreate() {
		User user = getUser(System.currentTimeMillis());

		webTestClient.post().uri(URI)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(user), User.class)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.username").isEqualTo(user.getUsername());
	}

	@Test
	public void testCreate_existingUsername() {
		User user = getUser();
		user = userService.save(user).block();
		webTestClient.post().uri(URI)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(user), User.class)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
				.expectBody()
				.jsonPath("$.message").isNotEmpty();
		;
	}

	@Test
	public void testGetAll() {
		webTestClient.get().uri(URI)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(User.class);
	}

	@Test
	public void testGetSingle() {
		User user = getUser();
		user = userService.save(user).block();

		webTestClient.get()
				.uri(URI_WITH_ID, Collections.singletonMap(PATH_VARIABLE_ID, user.getId()))
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.consumeWith(response ->
						Assertions.assertThat(response.getResponseBody()).isNotNull());
	}

	@Test
	public void testGetSingle_notFound() {
		webTestClient.get()
				.uri(URI_WITH_ID, Collections.singletonMap(PATH_VARIABLE_ID, 1))
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void testUpdate() {
		User user = getUser();
		user = userService.save(user).block();
		User updateUser = user;
		updateUser.setUsername("username_" + System.currentTimeMillis());

		webTestClient.put()
				.uri(URI_WITH_ID, Collections.singletonMap(PATH_VARIABLE_ID, user.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(updateUser), User.class)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.username").isEqualTo(updateUser.getUsername());
	}

	@Test
	public void testUpdate_notFound() {
		User updateUser = new User();

		webTestClient.put()
				.uri(URI_WITH_ID, Collections.singletonMap(PATH_VARIABLE_ID, 1))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(updateUser), User.class)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void testDelete() {
		User user = getUser();
		user = userService.save(user).block();

		webTestClient.delete()
				.uri(URI_WITH_ID, Collections.singletonMap(PATH_VARIABLE_ID,  user.getId()))
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void testDelete_notFound() {
		webTestClient.delete()
				.uri(URI_WITH_ID, Collections.singletonMap(PATH_VARIABLE_ID,  1))
				.exchange()
				.expectStatus().isNotFound();
	}
}
