package com.ox.core.client.config;

import com.ox.core.client.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain...");
        


        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> {
                // Add detailed request logging
                auth.requestMatchers(request -> {
                    log.info("Security check for request:");
                    log.info("  URI: {}", request.getRequestURI());
                    log.info("  URL: {}", request.getRequestURL());
                    log.info("  Context Path: {}", request.getContextPath());
                    log.info("  Servlet Path: {}", request.getServletPath());
                    log.info("  Path Info: {}", request.getPathInfo());
                    log.info("  Query String: {}", request.getQueryString());
                    log.info("  Method: {}", request.getMethod());
                    return false;
                }).permitAll();

                log.info("mb step 1");
                auth.requestMatchers(
                    new AntPathRequestMatcher("/auth/**"),  
                    new AntPathRequestMatcher("/swagger-ui/**"),
                    new AntPathRequestMatcher("/swagger-ui.html"),
                    new AntPathRequestMatcher("/v3/api-docs/**"),
                    new AntPathRequestMatcher("/h2-console/**")
                ).permitAll()
                .anyRequest().authenticated();
                log.info("mb step 2");
                log.info("Configured permitted paths without /api/v1 prefix as it's handled by context-path");
            })
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );

        log.info("Security filter chain configuration completed");
        return http.build();
    }

    @Bean
    public NoOpPasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
