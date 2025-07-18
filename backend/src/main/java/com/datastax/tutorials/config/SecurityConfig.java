package com.datastax.tutorials.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;
    
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
        delegate.setCsrfRequestAttributeName("_csrf");
        CsrfTokenRequestHandler handler = delegate::handle;
		
		http
    	.authorizeHttpRequests(a -> a
        		.requestMatchers("/", "/api/v1/users/", "/error", "/webjars/**").permitAll()
        		.anyRequest().authenticated()
        		//.anyRequest().permitAll()
        	)
        .formLogin(fl -> fl
        		.loginPage("/login").permitAll()
        	)
        .logout(l -> l
				.logoutUrl("/logout")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/")
				.permitAll()
            )
        .csrf(c -> c
               .csrfTokenRepository(tokenRepository)
               .csrfTokenRequestHandler(handler)
        	)
        .exceptionHandling(e -> e
            .authenticationEntryPoint(authEntryPoint)
        	)
        .oauth2Login()
			.defaultSuccessUrl("/", true);
		
		return http.build();
	}
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/api/v1/products/**",
                "/api/v1/categories/**",
                "/api/v1/categories/category/**",
                "/api/v1/prices/**",
                "/api/v1/featured/**",
                "/api/v1/carts/**",
                "/api/v1/users/**",
                //"/api/v1/user/**",	 // testing only...REMOVE or comment-out!
                "/api/v1/order/**",    // testing only...REMOVE or comment-out!
                "/api/v1/orderprocessor/**",
                "/swagger-ui/**",
				"/logout",
				"/static/**",
				"/index.html",
				"/images/**",
				"/favicon.ico",
				"/manifest.json",
                "/v3/api-docs/**",
                "/configuration/**",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/products/**",
                "/categories/**");
	}
}
