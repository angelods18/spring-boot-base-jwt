package it.angelodesantis.controller;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;

import it.angelodesantis.business.service.UserService;
import it.angelodesantis.exceptions.PreconditionFailedException;
import it.angelodesantis.exceptions.ValidationFailedException;
import it.angelodesantis.security.JwtTokenUtil;
import it.angelodesantis.security.jwt.JwtAuthenticationResponse;
import it.angelodesantis.security.jwt.JwtAuthenticationTokenRequest;
import it.angelodesantis.security.jwt.JwtRefreshTokenDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping(value="auth")
@Log4j2
public class AuthController {

	@Value("${jwt.header}")
	private String tokenHeader;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;
	
	
	@PostMapping("/signin")
	public JwtAuthenticationResponse signin(
			@RequestBody JwtAuthenticationTokenRequest request, HttpServletResponse response) 
		throws UsernameNotFoundException, ValidationFailedException{
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getEmail(),
							request.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}catch (Exception e) {
			log.error("Errore in login " + e);
		}
				
		
		final UserDetails userDetails =
				userDetailsService.loadUserByUsername(request.getEmail());
		if(passwordEncoder.matches(request.getPassword(), userDetails.getPassword())){
			List<String> authList = userDetails.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList());
			
			final String token = JwtTokenUtil.createAccessToken(userDetails.getUsername(), userDetails.getPassword(), 
					authList);
			final String refreshToken = JwtTokenUtil.createRefreshToken(userDetails.getUsername());
			response.setHeader(tokenHeader, token);
			return new JwtAuthenticationResponse(userDetails.getUsername(), userDetails.getAuthorities(), token, refreshToken);
		}else {
			throw new ValidationFailedException( "credenziali non corrette", "login");
		}
	}

	@PostMapping("/refresh")
	public JwtRefreshTokenDTO refresh(@RequestBody JwtRefreshTokenDTO request) throws ValidationFailedException, PreconditionFailedException {
		try {
			String refreshedToken = JwtTokenUtil.refreshToken(request.getAccessToken(), request.getRefreshToken());
			request.setAccessToken(refreshedToken);
			return request;
		} catch (JOSEException | BadJOSEException | ParseException e) {
			throw new ValidationFailedException("error", "refresh");
		}
	}
	
}
