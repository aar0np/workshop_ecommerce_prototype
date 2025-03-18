package com.datastax.tutorials.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http
    	.authorizeHttpRequests(a -> a
        		.antMatchers("/", "/api/v1/users/", "/error", "/webjars/**").permitAll()
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
               .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        	)
        .exceptionHandling(e -> e
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        	)
        .oauth2Login()
			.defaultSuccessUrl("/", true);
	}
	
	@Override
	public void configure(WebSecurity web) {
    	web
		.ignoring().antMatchers("/api/v1/products/**",
				                "/api/v1/categories/**",
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
				                "/swagger-ui.html");
	}
}
