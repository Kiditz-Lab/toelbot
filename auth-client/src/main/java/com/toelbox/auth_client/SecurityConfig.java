package com.toelbox.auth_client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorizeRequests ->
						authorizeRequests
								.requestMatchers("/", "/login", "/oauth2/**", "/logout").permitAll()  // Make sure the home and login pages are public
								.anyRequest().authenticated()  // Require authentication for other pages
				)
				.oauth2Login(oauth2Login ->
						oauth2Login
								.loginPage("/login")  // Optional: specify custom login page
								.defaultSuccessUrl("/", true)  // Redirect to home page after successful login
				)
				.logout(logout ->
						logout
								.logoutUrl("/logout")
								.logoutSuccessUrl("/")  // Redirect to home page after logout
				);

		return http.build();
	}

}
