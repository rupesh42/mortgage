package com.example.mortgage.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Value("#{${app.security.users}}")
	private Map<String, Map<String, String>> users;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder encoder) {
		var manager = new InMemoryUserDetailsManager();
		users.forEach((username, attrs) -> {
			var pwd = attrs.get("password");
			var role = attrs.getOrDefault("role", "USER");
			manager.createUser(User.withUsername(username).password(encoder.encode(pwd)).roles(role).build());
		});
		return manager;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/h2-console/**", "/swagger-ui*/**", "/swagger-ui/**", "/v3/api-docs/**",
						"/api-docs/**")
				.permitAll().requestMatchers("/mortgage-check").hasAnyRole("USER", "ADMIN").anyRequest()
				.authenticated()).httpBasic(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
				.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
		return http.build();
	}
}
