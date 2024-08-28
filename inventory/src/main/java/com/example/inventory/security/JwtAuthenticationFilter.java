package com.example.inventory.security;

import com.example.inventory.dto.UserDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header
        String authorizationHeader = request.getHeader("Authorization");

        // Check if header is present and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract token from header
            String token = authorizationHeader.substring(7);

            // Validate the JWT
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid token\"}");
                return;
            }

            // Get user information from the JWT
            UserDto userDto = jwtUtil.getUserDtoFromToken(token);
            
            // Create an authentication token with no roles
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                    userDto, token, null);
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication to SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
