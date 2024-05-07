package com.example.dreamvalutbackend.config.oauth2.domain;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo {
	private String socialId;
	private String email;
	private String name;
	private String image;

	public GoogleUserInfo(Map<String, Object> attributes) {
		this.socialId = String.valueOf(attributes.get("sub"));
		this.email = String.valueOf(attributes.get("email"));
		this.name = String.valueOf(attributes.get("name"));
		this.image = String.valueOf(attributes.get("picture"));
	}

	public String getSocialId() {
		return socialId;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}
}