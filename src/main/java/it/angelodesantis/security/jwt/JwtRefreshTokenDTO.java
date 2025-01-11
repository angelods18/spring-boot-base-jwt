package it.angelodesantis.security.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtRefreshTokenDTO {

	@NotBlank
	private String accessToken;
	@NotBlank
	private String refreshToken;
}
