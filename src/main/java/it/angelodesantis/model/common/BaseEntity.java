package it.angelodesantis.model.common;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, unique = true, updatable = false, length = 36)
	private UUID id;
}
