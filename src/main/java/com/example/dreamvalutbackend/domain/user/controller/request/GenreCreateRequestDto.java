package com.example.dreamvalutbackend.domain.user.controller.request;

import com.example.dreamvalutbackend.domain.user.domain.User;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GenreCreateRequestDto {
	@NotNull
	private Long userId;
}
