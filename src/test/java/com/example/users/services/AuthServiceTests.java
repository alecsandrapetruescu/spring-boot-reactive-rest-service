package com.example.users.services;

import com.example.users.security.dto.Authenticated;
import com.example.users.security.service.AuthService;
import com.example.users.user.models.User;
import com.example.users.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthServiceTests {

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceTests.class);

	@Autowired
	AuthService service;

	@MockBean
	private UserRepository repository;

	@MockBean
	PasswordEncoder encoder;

	@Test
	public void testAuthenticate() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(userName, password);

		User user = new User();
		user.setId("1");
		user.setUsername(userName);
		user.setPassword(encoder.encode(password));

		given(repository.findByUsername(userName)).willReturn(Mono.just(user));
		Authenticated authentication = service.authenticate(request).block();
		assertNotNull(authentication);
	}


	@Test
	public void testAuthenticate_UsernameNotFoundException() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(userName, password);

		User user = new User();
		user.setId("1");
		user.setUsername(userName);

		given(repository.findByUsername(userName)).willReturn(Mono.empty());

		assertThrows(UsernameNotFoundException.class, () -> service.authenticate(request));
	}

	@Test
	public void testAuthenticate_BadCredentialsException() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(userName, password);

		User user = new User();
		user.setId("1");
		user.setUsername(userName);

		given(repository.findByUsername(userName)).willReturn(Mono.empty());

		assertThrows(BadCredentialsException.class, () -> service.authenticate(request));
	}


	@Test
	public void testRegister() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(userName, password);

		User user = new User();
		user.setId("1");
		user.setUsername(userName);
		String encodedPassword = encoder.encode(password);
		user.setPassword(encodedPassword);

		when(repository.findByUsername(userName))
				.thenAnswer(new Answer<Object>() {
					@Override
				public Object answer(InvocationOnMock invocation) throws Throwable {
						user.setNew(true);
						return Mono.just(user);
				}
				});

		Mockito.atMost(2);

		User authentication = service.register(request).block();
		assertNotNull(authentication);
	}

	@Test
	public void testRegister_BadCredentialsException() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(userName, password);

		User user = new User();
		user.setId("1");
		user.setUsername(userName);
		user.setPassword(encoder.encode(password));

		given(repository.findByUsername(userName)).willReturn(Mono.just(user));

		assertThrows(BadCredentialsException.class, () -> service.register(request).block());
	}

}
