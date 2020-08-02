package com.example.users.components;

import com.example.users.security.configurations.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TokenProviderTests {

	@Autowired
	TokenProvider tokenProvider;

	@Test
	public void testGenerateToken() {
		String userName = "username_" + System.currentTimeMillis();
		String password = "password";
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, password);

		String token = tokenProvider.generateToken(authenticationToken);
		assertNotNull("Generated token must not be null!", token);
		assertEquals("Username from token is not equal to provided user", userName, tokenProvider.getUsernameFromToken(token));
	}


}
