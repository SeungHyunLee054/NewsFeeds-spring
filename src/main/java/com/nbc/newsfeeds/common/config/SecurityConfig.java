package com.nbc.newsfeeds.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nbc.newsfeeds.common.filter.AccessTokenFilter;
import com.nbc.newsfeeds.common.filter.JwtExceptionFilter;
import com.nbc.newsfeeds.common.filter.RefreshTokenFilter;
import com.nbc.newsfeeds.common.jwt.core.JwtService;

import at.favre.lib.crypto.bcrypt.BCrypt;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final RefreshTokenFilter refreshTokenFilter;
	private final AccessTokenFilter accessTokenFilter;
	private final JwtExceptionFilter jwtExceptionFilter;

	public SecurityConfig(JwtService jwtService) {
		this.refreshTokenFilter = new RefreshTokenFilter(jwtService);
		this.accessTokenFilter = new AccessTokenFilter(jwtService);
		this.jwtExceptionFilter = new JwtExceptionFilter();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEncoder() {
			@Override
			public String encode(CharSequence rawPassword) {
				return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, rawPassword.toString().toCharArray());
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toString().toCharArray(), encodedPassword);
				return result.verified;
			}
		};
	}

	@Bean
	@Profile("!test")
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/",
					"/swagger-ui/**",
					"/swagger-resources/**",
					"/v2/**",
					"/v3/**",
					"/webjars/**",
					"/**/auth/signin",
					"/**/auth/signup"
				).permitAll()
				.anyRequest().authenticated())
			.addFilterBefore(refreshTokenFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(accessTokenFilter, RefreshTokenFilter.class)
			.addFilterBefore(jwtExceptionFilter, AccessTokenFilter.class)
			.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws
		Exception {
		return auth.getAuthenticationManager();
	}
}
