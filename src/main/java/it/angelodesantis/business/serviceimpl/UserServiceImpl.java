package it.angelodesantis.business.serviceimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.angelodesantis.business.service.UserService;
import it.angelodesantis.model.User;
import it.angelodesantis.repository.UserRepository;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("find user with username {}", username);
		Optional<User> user = userRepository.findByEmail(username);
		if(user.isEmpty()) {
			String msg = String.format("User %s not found", username);
			log.error("user {} not found", username);
			throw new UsernameNotFoundException(msg);
		}else {
			log.debug("user {} found", username);
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
			user.get().getRoles().forEach(r -> {
				authorities.add(new SimpleGrantedAuthority(r.getRole().name()));
			});
			return new org.springframework.security.core.userdetails.User(
				user.get().getEmail(), user.get().getPassword(), authorities
			);
		}
	}
}
