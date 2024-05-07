package com.example.dreamvalutbackend.config.oauth2.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.dreamvalutbackend.config.oauth2.domain.GoogleUserInfo;
import com.example.dreamvalutbackend.config.oauth2.domain.KakaoUserInfo;
import com.example.dreamvalutbackend.config.oauth2.domain.NaverUserInfo;
import com.example.dreamvalutbackend.config.oauth2.domain.OAuth2UserInfo;
import com.example.dreamvalutbackend.domain.user.domain.SocialType;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserDetailPrincipal;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);
		Map<String, Object> attributes = oAuth2User.getAttributes();
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		SocialType socialType = SocialType.valueOf(registrationId.toUpperCase());

		OAuth2UserInfo userInfo;
		switch (registrationId) {
			case "google":
				userInfo = (OAuth2UserInfo)new GoogleUserInfo(attributes);
				break;
			case "naver":
				userInfo = (OAuth2UserInfo)new NaverUserInfo(attributes);
				break;
			case "kakao":
			default:
				userInfo = (OAuth2UserInfo)new KakaoUserInfo(attributes);
				break;
		}

		String socialId = userInfo.getSocialId();
		String name = userInfo.getName();
		String email = userInfo.getEmail();
		String profile = userInfo.getImage();
		String displayName = userInfo.getName();


		// 소셜 ID 로 사용자를 조회, 없으면 socialId 와 이름으로 사용자 생성
		Optional<User> bySocialId = userRepository.findBySocialId(socialId);
		User user = bySocialId.orElseGet(() -> saveSocialMember(socialId, name, email, profile, displayName, socialType));

		return new UserDetailPrincipal(user, Collections.singleton(new SimpleGrantedAuthority(user.getRole().getValue())),attributes);
	}

	public User saveSocialMember(String socialId, String name, String email, String profile, String displayName, SocialType socialType) {
		User newMember = User.builder().socialId(socialId).userName(name).userEmail(email).profileImage(profile).role(UserRole.USER).displayName(displayName).type(socialType).build();
		return userRepository.save(newMember);
	}


}
