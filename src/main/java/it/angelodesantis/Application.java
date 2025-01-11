package it.angelodesantis;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.angelodesantis.model.Role;
import it.angelodesantis.model.User;
import it.angelodesantis.model.enums.ERole;
import it.angelodesantis.repository.RoleRepository;
import it.angelodesantis.repository.UserRepository;

@SpringBootApplication
@EnableAutoConfiguration
public class Application {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	@Bean
	CommandLineRunner loadData(UserRepository userRepository, RoleRepository roleRepository) {
		return (args) -> {
			List<ERole> startRoles = List.of(ERole.ROLE_BASIC, ERole.ROLE_ADMIN);
			// ruoli
			List<Role> roles = roleRepository.findByRoleIn(startRoles);
			Role adminRole = null;
			if(roles.stream().noneMatch(r -> r.getRole().equals(ERole.ROLE_ADMIN))){
				Role role = new Role();
				role.setRole(ERole.ROLE_ADMIN);
				adminRole = roleRepository.save(role);
			} else {
				adminRole = roles.stream().filter(r -> r.getRole().equals(ERole.ROLE_ADMIN)).findFirst().get();
			}

			if(roles.stream().noneMatch(r -> r.getRole().equals(ERole.ROLE_BASIC))){
				Role role = new Role();
				role.setRole(ERole.ROLE_BASIC);
				roleRepository.save(role);
			}

			Optional<User> user = userRepository.findByEmail("angelods18@gmail.com");
			if(user.isEmpty()) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setEmail("angelods18@gmail.com");
				admin.setPassword(passwordEncoder().encode("Desa123!"));
				admin.setRoles(Arrays.asList(adminRole));
				admin.setEnabled(true);
				
				admin = userRepository.save(admin);
			}
		};
	}
}
