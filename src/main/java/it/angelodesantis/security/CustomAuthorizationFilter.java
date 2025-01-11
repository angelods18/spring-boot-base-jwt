package it.angelodesantis.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;
        if(request.getServletPath().equals("/auth/login") || request.getServletPath().equals("/auth/refresh-token")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader("Authorization");
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    token = authorizationHeader.substring("Bearer ".length());
                    UsernamePasswordAuthenticationToken authenticationToken = JwtTokenUtil.parseToken(token);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                }
                catch (Exception e) {
                    log.error(String.format("Error auth token: %s", token), e);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("errorMessage", e.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
