package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity //Before/After a request do an specific action ->
public class SecurityConfiguration {

    private MyUserDetailsService myUserDetailsService;

    @Autowired
    public void setMyUserDetailsService(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    //Establish the BCrypt Encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .requestMatchers(
                        "/auth/users",
                        "/auth/users/login",
                        "/auth/users/register",
                        "/auth/users/verify**",
                        "/auth/users/forgot-password**",   // Add this for password recovery request
                        "/auth/users/reset-password**",    // Add this for password reset
                        "/auth/users/request-password-reset",    // Add this for password reset
                        "/auth/users/change-password**",
                        "/swagger-ui**",
                        "/swagger-ui/**",
                        "/api-docs**",
                        "/api-docs/**"

                        // Add this for password change
                )
                .permitAll()
                .anyRequest().authenticated();

        http.sessionManagement(
                sessionAuthenticationStrategy ->
                        sessionAuthenticationStrategy.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Filter -> JWT Token

        http.addFilterBefore(authenticationJWTTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.csrf(csrf -> csrf.disable()); //Cross-Site Request Forgery

        return http.build();


    }

    @Bean
    public JwtRequestFilter authenticationJWTTokenFilter() {
        return new JwtRequestFilter();
    }

    //authenticate(): Authentication, Exception, null
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //This is charge of setting the service (which will go for the user info), setting the password encoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

}