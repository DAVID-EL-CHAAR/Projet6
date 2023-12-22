package com.payMyBuddy;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.CustomUserDetailsService;

@EnableWebSecurity(debug = true)
@Configuration
public class SecurityConfig {

	
	@Autowired
    private CustomUserDetailsService userDetailsService;

 
	    @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	           // .csrf(csrf -> csrf.disable()) // Toujours à désactiver en production si on a avez pas besoin.
	            .authorizeHttpRequests(authz -> authz
	            	.requestMatchers("/register", "/login").permitAll() 
	                .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                    .loginPage("/login")
	                    .loginProcessingUrl("/login")
	                    .usernameParameter("email") // Important si on utilisez 'email' au lieu de 'username'
	                    .passwordParameter("password")
	                    .defaultSuccessUrl("/home", true)
	                    .failureUrl("/login?error=true")
	                    .permitAll()
	                )
	                .logout(logout -> logout
	                	.logoutUrl("/logout")
	                    .logoutSuccessUrl("/login?logout=true")
	                    .permitAll()
	                )
	                .userDetailsService(userDetailsService);
	            
	            return http.build();
	        }

  
  
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
 
   
}


