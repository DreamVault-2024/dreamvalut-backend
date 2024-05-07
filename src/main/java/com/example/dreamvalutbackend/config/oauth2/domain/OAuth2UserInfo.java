package com.example.dreamvalutbackend.config.oauth2.domain;

public interface OAuth2UserInfo {
	String getSocialId();
	String getName();
	String getEmail();
	String getImage();
}
