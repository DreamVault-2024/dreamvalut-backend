package com.example.dreamvalutbackend.config.oauth2.domain;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
	public static String socialId;
	public static Map<String, Object> responseMap;
	public NaverUserInfo(Map<String, Object> attributes) {
		responseMap = (Map<String, Object>) attributes.get("response");
	}

	public String getSocialId() {
		return String.valueOf(responseMap.get("id"));
	}

	public String getEmail() {
		return String.valueOf(responseMap.get("email"));
	}
	public String getName() {
		return String.valueOf(responseMap.get("name"));
	}
	public String getImage() {
		return String.valueOf(responseMap.get("profile_image"));
	}




}
