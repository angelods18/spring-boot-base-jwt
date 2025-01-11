package it.angelodesantis.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import it.angelodesantis.model.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{
	
	@Column(unique = true, nullable = false)
	private String email;
	private String password;

	@Column(unique = true)
	private String username;
	private String name;
	private String surname;
	private LocalDate birthDate;
	private String gender;
	
	private boolean enabled = true;
	
	private LocalDateTime creationDate;
	private LocalDateTime lastUpdate;
	private LocalDateTime lastAccess;
	
	@ManyToMany
	@JoinTable(name="user_roles",
		joinColumns = @JoinColumn(name="id_user", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name="id_role", referencedColumnName = "id"))
	private List<Role> roles;

}
