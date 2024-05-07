package com.example.dreamvalutbackend.config.jwt.filter;

import com.example.dreamvalutbackend.config.exception.CustomExpiredJwtException;
import com.example.dreamvalutbackend.config.exception.CustomJwtException;
import com.example.dreamvalutbackend.config.jwt.utils.JwtConstants;
import com.example.dreamvalutbackend.config.jwt.utils.JwtUtils;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
public class JwtVerifyFilter extends OncePerRequestFilter {
	private static final String[] whitelist = {"/signUp", "/login" , "/refresh", "/tracks/**", "/playlists/**", "/tags/**", "/genres/**", "/users/**", "/users","/search"};

	private static void checkAuthorizationHeader(String header) {
		if(header == null) {
			throw new CustomJwtException("토큰이 전달되지 않았습니다");
		} else if (!header.startsWith(JwtConstants.JWT_TYPE)) {
			throw new CustomJwtException("BEARER 로 시작하지 않는 올바르지 않은 토큰 형식입니다");
		}
	}

	// 필터를 거치지 않을 URL 을 설정하고, true 를 return 하면 현재 필터를 건너뛰고 다음 필터로 이동
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String requestURI = request.getRequestURI();
		return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String authHeader = request.getHeader(JwtConstants.JWT_HEADER);

		try {
			checkAuthorizationHeader(authHeader);   // header 가 올바른 형식인지 체크
			String token = JwtUtils.getTokenFromHeader(authHeader);
			Authentication authentication = JwtUtils.getAuthentication(token);


			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);    // 다음 필터로 이동
		} catch (Exception e) {
			Gson gson = new Gson();
			String json = "";
			if (e instanceof CustomExpiredJwtException) {
				json = gson.toJson(Map.of("Token_Expired", e.getMessage()));
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			} else {
				json = gson.toJson(Map.of("error", e.getMessage()));
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}

			response.setContentType("application/json; charset=UTF-8");
			PrintWriter printWriter = response.getWriter();
			printWriter.println(json);
			printWriter.close();
		}
	}
}
