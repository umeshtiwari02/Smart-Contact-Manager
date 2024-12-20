package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

	@Bean
	UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(getUserDetailsService());
		provider.setPasswordEncoder(passwordEncoder());

		return provider;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(
						registry -> registry.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/user/**")
								.hasRole("USER").requestMatchers("/**").permitAll().anyRequest().authenticated())
				.formLogin(login -> login.loginPage("/signin").loginProcessingUrl("/do_login")
						.defaultSuccessUrl("/user/index").permitAll());
		// Methods to Configure the behavior of Login page in Spring Security
		// methods are :: login
		// (1) .loginPage("/signin")
		// (2) .loginProcessingUrl("/do_login")
		// (3) .defaultSuccessUrl("/user/index")
		// (4) .failureUrl("/login_fail")

		return httpSecurity.build();
	}

}
