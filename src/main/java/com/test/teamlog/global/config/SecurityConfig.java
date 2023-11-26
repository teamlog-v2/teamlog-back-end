package com.test.teamlog.global.config;

import com.test.teamlog.global.security.JwtAuthenticationEntryPoint;
import com.test.teamlog.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(t -> t.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(t -> {
                    t.requestMatchers(HttpMethod.POST, "/api/accounts/sign-in", "/api/accounts/sign-up", "/api/tokens/reissue").permitAll()
                            .requestMatchers(HttpMethod.GET, "/resources/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/downloadFile/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll()
                            .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/v2/api-docs", "...");
    }
}
