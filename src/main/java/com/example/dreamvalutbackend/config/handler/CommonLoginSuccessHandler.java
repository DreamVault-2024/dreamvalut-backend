package com.example.dreamvalutbackend.config.handler;

import com.example.dreamvalutbackend.config.jwt.utils.JwtConstants;
import com.example.dreamvalutbackend.config.jwt.utils.JwtUtils;
import com.example.dreamvalutbackend.domain.user.domain.UserDetailPrincipal;
import com.example.dreamvalutbackend.domain.user.repository.UserGenreRepository;
import com.example.dreamvalutbackend.redis.domain.Token;
import com.example.dreamvalutbackend.redis.repository.TokenRepository;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${domain.frontend}")
	private String frontendUrl;

	private final TokenRepository tokenRepository;
	private final UserGenreRepository userGenreRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

		UserDetailPrincipal principal = (UserDetailPrincipal) authentication.getPrincipal();

		Map<String, Object> responseMap = principal.getUserInfo();

		String accessToken = JwtUtils.generateToken(responseMap, JwtConstants.ACCESS_EXP_TIME);
		String refreshToken = JwtUtils.generateToken(responseMap, JwtConstants.REFRESH_EXP_TIME);

		String userId = String.valueOf(principal.getUserId());
		Token token = new Token(userId, refreshToken);
		tokenRepository.save(token);

		String targetUrl = determineTargetUrl(request, response, accessToken, refreshToken, Long.valueOf(userId));

		getRedirectStrategy().sendRedirect(request, response, targetUrl);

	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, String accessToken, String refreshToken, Long userId) {
		String redirectUri = request.getParameter("redirect_uri");
		// if (redirectUri == null) {
		// 	redirectUri = getDefaultTargetUrl();
		// }

		boolean hasGenres = userGenreRepository.existsByUser_UserId(userId);

		if (hasGenres) {
			redirectUri = frontendUrl + "/main";
		} else {
			redirectUri = frontendUrl + "/genre_select";
		}



		return UriComponentsBuilder.fromUriString(redirectUri)
			.queryParam("accessToken", accessToken)
			.queryParam("refreshToken", refreshToken)
			.build().toUriString();
	}


}

