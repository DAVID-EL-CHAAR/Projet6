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
	           // .csrf(csrf -> csrf.disable()) // Toujours à désactiver en production si vous n'en avez pas besoin.
	            .authorizeHttpRequests(authz -> authz
	            	.requestMatchers("/register", "/login").permitAll() // Simplifie l'utilisation des antMatchers.
	                .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                    .loginPage("/login")
	                    .loginProcessingUrl("/login")
	                    .usernameParameter("email") // Important si vous utilisez 'email' au lieu de 'username'
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
	            /*
	            .formLogin(form -> form
	                .loginPage("/login")// Si vous avez une page de connexion personnalisée.
	                .usernameParameter("email")
	                .loginProcessingUrl("/login") // URL de traitement du formulaire de connexion.
	                .defaultSuccessUrl("/home", true) // Redirection après connexion réussie.
	                .failureUrl("/login?error=true") // Redirection après échec de connexion.
	                .permitAll()
	            )
	            .logout(logout -> logout
	                .logoutUrl("/logout") // URL de déconnexion.
	                .logoutSuccessUrl("/login?logout=true") // Redirection après déconnexion réussie.
	                .permitAll()
	            );

	        return http.build();
	    }

*/
  
  
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    /*
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
*/

   
}


