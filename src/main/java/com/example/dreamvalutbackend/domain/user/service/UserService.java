package com.example.dreamvalutbackend.domain.user.service;

import org.springframework.stereotype.Service;

import com.example.dreamvalutbackend.domain.user.controller.response.UserInfoResponseDto;
import com.example.dreamvalutbackend.domain.user.controller.response.UserResponseDto;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	// public UserResponseDto userInfoList(UserInfoResponseDto userInfoResponseDto) {
	//
	// }

}
