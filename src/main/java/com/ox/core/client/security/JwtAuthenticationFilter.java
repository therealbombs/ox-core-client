package com.ox.core.client.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            log.debug("Processing request to {}", request.getRequestURI());
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String username;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No valid authorization header found for request to {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("Found authorization header for request to {}", request.getRequestURI());
            jwt = authHeader.substring(7);
            
            try {
                username = jwtTokenProvider.extractUsername(jwt);
                log.debug("Extracted username from token: {}", username);
            } catch (Exception e) {
                log.error("Error extracting username from token: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Loading user details for username: {}", username);
                try {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    
                    if (jwtTokenProvider.isTokenValid(jwt, userDetails)) {
                        log.debug("Token is valid for user: {}", username);
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("Successfully set authentication in SecurityContext for user: {}", username);
                    } else {
                        log.warn("Token validation failed for user: {}", username);
                    }
                } catch (Exception e) {
                    log.error("Error during authentication process: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Unexpected error in JWT filter: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }
}
