package com.example.users.components;

import com.example.users.security.configurations.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TokenProviderTests {

	@InjectMocks
	TokenProvider tokenProvider;

	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "demoSecretKey");
		ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", 86400000);
		ReflectionTestUtils.setField(tokenProvider, "authoritiesKeys", "scopes");
	}

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
