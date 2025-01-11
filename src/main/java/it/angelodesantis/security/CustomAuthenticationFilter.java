package it.angelodesantis.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final String BAD_CREDENTIAL_MESSAGE = "Authentication failed";
	
	private final AuthenticationManager authenticationManager;
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = null;
		String password = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String,String> map = objectMapper.readValue(request.getInputStream(), Map.class);
			username = map.get("username");
			password = map.get("password");
			log.debug("login with {} " + username);
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (AuthenticationException e) {
			log.error(BAD_CREDENTIAL_MESSAGE, username, password);
			throw e;
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			Map<String, String> error = new HashMap<>();
            error.put("errorMessage", e.getMessage());
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
            throw new RuntimeException(String.format("Error in attemptAuthentication with username %s and password %s", username, password), e);
		}
	}
	
	@Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        User user = (User)authentication.getPrincipal();
        String accessToken = JwtTokenUtil.createAccessToken(user.getUsername(), request.getRequestURL().toString(),
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        String refreshToken = JwtTokenUtil.createRefreshToken(user.getUsername());
        response.addHeader("access_token", accessToken);
        response.addHeader("refresh_token", refreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", "Bad credentials");
        response.setContentType("application/json");
        mapper.writeValue(response.getOutputStream(), error);
    }
	
}
