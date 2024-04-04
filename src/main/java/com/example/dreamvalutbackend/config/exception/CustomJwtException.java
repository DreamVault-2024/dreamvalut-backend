package com.example.dreamvalutbackend.config.exception;


public class CustomJwtException extends RuntimeException {
	public CustomJwtException(String msg) {
		super(msg);
	}
}

