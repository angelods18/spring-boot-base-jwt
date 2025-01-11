package it.angelodesantis.security;

import java.util.List;

import org.springframework.context.annotation.Bean;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfig {

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration authenticationConfiguration)  throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
	@SuppressWarnings("removal")
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-resources", "/swagger-ui**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/public/**", "/test/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/public/**", "/test/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/signin", "/auth/signup", "/auth/refresh").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/auth/password-recovery").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilter(new CustomAuthenticationFilter(authenticationManager))
                .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.cacheControl());
		
		CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration
                .setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:8100", "http://localhost:8000"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));


        http
                .cors(cors -> cors.configurationSource(request -> corsConfiguration))
                .httpBasic(t -> t.disable());
		
		return http.build();
	}
}
