package it.angelodesantis.security.jwt;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private final String username;
	Collection<? extends GrantedAuthority> authorities;
	private final String accessToken;
	private final String refreshToken;

    public JwtAuthenticationResponse(String username, Collection<? extends GrantedAuthority> authorities, String token, String refreshToken) {
        this.username = username;
        this.authorities = authorities;
        this.accessToken=token;
        this.refreshToken=refreshToken;
    }

    public String getUsername() {
        return this.username;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
    
    public String getAccessToken() {
    	return this.accessToken;
    }
    
    public String getRefreshToken() {
		return this.refreshToken;
	}
}