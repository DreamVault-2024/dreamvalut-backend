package com.example.dreamvalutbackend.config.handler;

import com.example.dreamvalutbackend.config.jwt.utils.JwtConstants;
import com.example.dreamvalutbackend.config.jwt.utils.JwtUtils;
import com.example.dreamvalutbackend.domain.user.domain.UserDetailPrincipal;
import com.example.dreamvalutbackend.redis.domain.Token;
import com.example.dreamvalutbackend.redis.repository.TokenRepository;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonLoginSuccessHandler implements AuthenticationSuccessHandler {

	private final TokenRepository tokenRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		UserDetailPrincipal principal = (UserDetailPrincipal) authentication.getPrincipal();

		Map<String, Object> responseMap = principal.getUserInfo();

		String refreshToken = JwtUtils.generateToken(responseMap, JwtConstants.REFRESH_EXP_TIME);

		responseMap.put("accessToken", JwtUtils.generateToken(responseMap, JwtConstants.ACCESS_EXP_TIME));
		responseMap.put("refreshToken", refreshToken);

		Long userId = principal.getUserId();
		Token token = new Token(refreshToken, userId);
		tokenRepository.save(token);


		Gson gson = new Gson();
		String json = gson.toJson(responseMap);

		response.setContentType("application/json; charset=UTF-8");

		PrintWriter writer = response.getWriter();
		writer.println(json);
		writer.flush();
	}

}

