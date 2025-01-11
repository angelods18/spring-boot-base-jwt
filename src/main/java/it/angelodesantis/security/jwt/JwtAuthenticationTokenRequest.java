package it.angelodesantis.security.jwt;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationTokenRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;
    private String password;


}