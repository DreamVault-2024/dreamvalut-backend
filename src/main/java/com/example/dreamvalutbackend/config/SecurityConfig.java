package com.example.dreamvalutbackend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.dreamvalutbackend.config.handler.CommonLoginSuccessHandler;
import com.example.dreamvalutbackend.config.jwt.filter.JwtVerifyFilter;
import com.example.dreamvalutbackend.config.oauth2.service.OAuth2UserService;
import com.example.dreamvalutbackend.redis.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final OAuth2UserService oAuth2UserService;
	private final TokenRepository tokenRepository;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer :: disable)
			.cors(cors -> cors.configurationSource(corsConfiguration()))
			.sessionManagement(httpSecuritySessionManagementConfigurer -> {
				httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
			}) // 세션 생성 X
			.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
				authorizationManagerRequestMatcherRegistry.anyRequest().permitAll()) //모든 요청 인증없이 허용
			.addFilterBefore(jwtVerifyFilter(), UsernamePasswordAuthenticationFilter.class)
			// .authenticationProvider(authenticationProvider()).addFilterBefore(jwtAuthFIlter, UsernamePasswordAuthenticationFilter.class);
			.formLogin(httpSecurityFormLoginConfigurer ->
				httpSecurityFormLoginConfigurer
					.loginPage("/login"))
			.oauth2Login(httpSecurityOAuth2LoginConfigurer ->
				httpSecurityOAuth2LoginConfigurer.loginPage("/oauth2/login")
					.successHandler(commonLoginSuccessHandler())
					.userInfoEndpoint(userInfoEndpointConfig ->
						userInfoEndpointConfig.userService(oAuth2UserService)))
			.build();



	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtVerifyFilter jwtVerifyFilter() {
		return new JwtVerifyFilter();
	}
	@Bean
	public CommonLoginSuccessHandler commonLoginSuccessHandler() {
		return new CommonLoginSuccessHandler(tokenRepository);
	}



	@Bean
	CorsConfigurationSource corsConfiguration() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000"));
		configuration.addAllowedHeader("*"); //corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
		configuration.addAllowedMethod("*"); //corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",configuration); //// 모든 경로에 대해서 CORS 설정을 적용
		return urlBasedCorsConfigurationSource;
	}

}
