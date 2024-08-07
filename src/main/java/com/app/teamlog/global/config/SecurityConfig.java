package com.app.teamlog.global.config;

import com.app.teamlog.global.auth.CustomOAuth2SuccessHandler;
import com.app.teamlog.global.auth.CustomOAuth2UserService;
import com.app.teamlog.global.exception.filter.ExceptionHandlerFilter;
import com.app.teamlog.global.security.JwtAuthenticationEntryPoint;
import com.app.teamlog.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(t -> t.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(t -> {
                    t.requestMatchers(HttpMethod.POST, "/api/accounts/sign-in", "/api/accounts/sign-up", "/api/tokens/reissue").permitAll()
                            .requestMatchers(HttpMethod.GET, "/resources/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/downloadFile/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll()
                            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                            .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class);

        http
                .oauth2Login(
                        auth -> auth.authorizationEndpoint(req -> req.baseUri("/login"))
                                .userInfoEndpoint(config -> config.userService(customOAuth2UserService))
                                .successHandler(customOAuth2SuccessHandler)
                );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/v2/api-docs", "...");
    }
}
