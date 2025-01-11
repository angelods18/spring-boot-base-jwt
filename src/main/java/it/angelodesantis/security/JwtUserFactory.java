package it.angelodesantis.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import it.angelodesantis.model.Role;
import it.angelodesantis.model.User;
import it.angelodesantis.security.jwt.JwtUser;

public class JwtUserFactory {

	private JwtUserFactory() {
		
	}
	
	public static JwtUser create(User user) {
		return new JwtUser(user.getUsername(),
				user.getPassword(),
				mapToGrantedAuthorities(user.getRoles()),
				true
		);
	}
	
	private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> roles){
		return roles.stream()
				.map(role -> 
				new SimpleGrantedAuthority(role.getRole().name()))
				.collect(Collectors.toList());
	}
}