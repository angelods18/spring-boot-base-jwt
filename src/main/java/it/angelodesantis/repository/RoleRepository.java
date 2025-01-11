package it.angelodesantis.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import it.angelodesantis.model.Role;
import java.util.List;
import java.util.Optional;

import it.angelodesantis.model.enums.ERole;


public interface RoleRepository extends JpaRepository<Role, UUID>{
    
    Optional<Role> findByRole(ERole role);

    List<Role> findByRoleIn(List<ERole> roles);
}
