package it.angelodesantis.model;

import it.angelodesantis.model.common.BaseEntity;
import it.angelodesantis.model.enums.ERole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="roles")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity{

	@Enumerated(EnumType.STRING)
	private ERole role;

}
