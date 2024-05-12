package com.example.dreamvalutbackend.domain.user.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class UserDetailPrincipal implements OAuth2User {

	private User user;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;

	public UserDetailPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
		this.user = user;
		this.authorities = authorities;
	}

	public UserDetailPrincipal(User user, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
		this.user = user;
		this.authorities = authorities;
		this.attributes = attributes;
	}

	// OAuth2User 메소드 구현
	@Override
	public String getName() {
		return user.getUserName();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	// 추가 메소드: 사용자 정보를 토큰에 포함시키기 위한 메소드 등
	public Map<String, Object> getUserInfo() {
		Map<String, Object> info = new HashMap<>();
		info.put("name", user.getUserName());
		info.put("email", user.getUserEmail());
		info.put("role", user.getRole().toString());
		info.put("userId", user.getUserId().toString());
		return info;
	}

	public Long getUserId() {
		return user.getUserId();
	}
}