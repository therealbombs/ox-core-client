package com.ox.core.client.security;

import com.ox.core.client.service.ClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ClientService clientService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            log.debug("Processing request to {}", request.getRequestURI());
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No valid authorization header found for request to {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("Found authorization header for request to {}", request.getRequestURI());
            final String jwt = authHeader.substring(7);
            
            try {
                String clientId = jwtTokenProvider.extractClientId(jwt);
                log.debug("Extracted clientId from token: {}", clientId);

                if (clientId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    log.debug("Validating client: {}", clientId);
                    
                    if (clientService.validateClient(clientId) && jwtTokenProvider.isTokenValid(jwt)) {
                        log.debug("Client validation successful for: {}", clientId);
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                clientId,
                                null,
                                Collections.emptyList()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("Successfully set authentication in SecurityContext for client: {}", clientId);
                    } else {
                        log.warn("Client validation failed for: {}", clientId);
                    }
                }
            } catch (Exception e) {
                log.error("Error processing token: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Unexpected error in JWT filter: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }
}
