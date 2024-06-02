package com.example.dreamvalutbackend.domain.user.controller.response;

import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponseDto {
	private Long userId;
	private String userName;
	private String displayName;
	private String userEmail;
	private String profileImage;
	private UserRole role;
	private String socialId;

	@Builder
	public UserResponseDto(Long userId, String userName, String displayName, String userEmail, String profileImage,
		UserRole role, String socialId) {
		this.userId = userId;
		this.userName = userName;
		this.displayName  = displayName;
		this.userEmail = userEmail;
		this.profileImage = profileImage;
		this.role = role;
		this.socialId = socialId;
	}

	public static UserResponseDto toDto(User user) {
		return UserResponseDto.builder()
			.userId(user.getId())
			.userName(user.getUserName())
			.displayName(user.getDisplayName())
			.userEmail(user.getUserEmail())
			.profileImage(user.getProfileImage())
			.role(user.getRole())
			.socialId(user.getSocialId())
			.build();
	}

}
