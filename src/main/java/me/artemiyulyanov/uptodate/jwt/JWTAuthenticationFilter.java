package me.artemiyulyanov.uptodate.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.artemiyulyanov.uptodate.mail.EmailVerificationCode;
import me.artemiyulyanov.uptodate.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JWTAuthenticationFilter extends OncePerRequestFilter {
    public static final String ERROR_TEMPLATE = "{\"error\": \"%s\"}";

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private JWTTokenService jwtTokenService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/auth") && !request.getRequestURI().endsWith("/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtTokenService.extractToken(request);

            if (token == null || !jwtTokenService.validateToken(token)) {
                throw new AuthenticationException("JWT token is missing!") {};
            }

            if (jwtTokenService.isTokenBlacklisted(token)) {
                throw new AuthenticationException("JWT token is invalid!") {};
            }

            if (jwtUtil.isTokenExpired(token)) {
                throw new AuthenticationException("JWT token is expired!") {};
            }

            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(String.format(ERROR_TEMPLATE, e.getMessage()));
        }
    }
}