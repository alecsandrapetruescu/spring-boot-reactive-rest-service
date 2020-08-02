package com.example.users.security.dto;

public class Authenticated {
	private String token;

	public Authenticated(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
