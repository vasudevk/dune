package io.arrakis.auth;


import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (request.getRequestURI().equalsIgnoreCase("/create-token-ttl") || request.getRequestURI().equalsIgnoreCase("/create-token")) {
            chain.doFilter(request, response);
            return;
        }

        if (header == null || !header.startsWith("Bearer ")) {
            SecurityContextHolder.clearContext();
            buildUnauthorizedError(response, "Token is missing");
        } else {
            String token = header.substring(7);
            try {
                DecodedJWT decodedJWT = JWTUtil.decode(token);
                User principal = new User(decodedJWT.getSubject(), "", Collections.emptyList());
                Authentication authentication = new UsernamePasswordAuthenticationToken(principal, token, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                buildUnauthorizedError(response, e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }

    private static void buildUnauthorizedError(HttpServletResponse response, String error) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + error + "\"}");
    }
}