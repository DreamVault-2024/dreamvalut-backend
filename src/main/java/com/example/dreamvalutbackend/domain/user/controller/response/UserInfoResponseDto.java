package com.example.dreamvalutbackend.domain.user.controller.response;

import com.example.dreamvalutbackend.domain.user.domain.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoResponseDto {
	private String userEmail;
	private String userName;
	private String displayName;
	private String profileImage;

	@Builder
	public UserInfoResponseDto(String userName, String displayName, String userEmail, String profileImage) {
		this.userName = userName;
		this.displayName  = displayName;
		this.userEmail = userEmail;
		this.profileImage = profileImage;
	}

	public static UserInfoResponseDto toDto(User user) {
		return UserInfoResponseDto.builder()
			.userName(user.getUserName())
			.displayName(user.getDisplayName())
			.userEmail(user.getUserEmail())
			.profileImage(user.getProfileImage())
			.build();
	}
}
