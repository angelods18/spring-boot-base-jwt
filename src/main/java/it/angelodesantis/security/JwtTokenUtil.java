package it.angelodesantis.security;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import it.angelodesantis.exceptions.PreconditionFailedException;


@Component
public abstract class JwtTokenUtil{
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Value("${jwt.prefix}")
	private String prefix;
	
	@Value("${jwt.expiration}")
    private Long expiration;
	
	private static String SECRET = "FBA898697394CDBC534E7ED86A97AA59F627FE6B309E0A21EEC6C9B130E0369C";
	private static int EXPIRE_HOUR_TOKEN = 24*7;
	private static int EXPIRE_HOUR_REFRESH_TOKEN= 24*30;
	
	public static String createAccessToken(String username, String issuer, List<String> roles) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issuer(issuer)
                    .claim("roles", roles)
                    .expirationTime(Date.from(Instant.now().plusSeconds(EXPIRE_HOUR_TOKEN * 3600)))
                    .issueTime(new Date())
                    .build();

            Payload payload = new Payload(claims.toJSONObject());

            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                    payload);

            jwsObject.sign(new MACSigner(SECRET));
            return jwsObject.serialize();
        }
        catch (JOSEException e) {
            throw new RuntimeException("Error to create JWT", e);
        }
    }

    public static String createRefreshToken(String username) {
    	 try {
             JWTClaimsSet claims = new JWTClaimsSet.Builder()
                     .subject(username)
                     .expirationTime(Date.from(Instant.now().plusSeconds(EXPIRE_HOUR_REFRESH_TOKEN * 3600)))
                     .issueTime(new Date())
                     .build();

             Payload payload = new Payload(claims.toJSONObject());

             JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                     payload);

             jwsObject.sign(new MACSigner(SECRET));
             return jwsObject.serialize();
         }
         catch (JOSEException e) {
             throw new RuntimeException("Error to create JWT", e);
         }
    }

    @SuppressWarnings("unchecked")
	public static UsernamePasswordAuthenticationToken parseToken(String token) throws JOSEException, ParseException,
            BadJOSEException {

        byte[] secretKey = SECRET.getBytes();
        SignedJWT signedJWT = SignedJWT.parse(token);
        signedJWT.verify(new MACVerifier(secretKey));
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.HS256,
                new ImmutableSecret<>(secretKey));
        jwtProcessor.setJWSKeySelector(keySelector);
        jwtProcessor.process(signedJWT, null);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        String username = claims.getSubject();
        var roles = (List<String>) claims.getClaim("roles");
        var authorities = roles == null ? null : roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
    
    public static JWTClaimsSet getClaimsFromToken(String token) throws JOSEException, BadJOSEException, ParseException {
    	 byte[] secretKey = SECRET.getBytes();
         SignedJWT signedJWT = SignedJWT.parse(token);
         signedJWT.verify(new MACVerifier(secretKey));

         JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
         return claims;
    }
    
    public static Boolean isTokenExpired(String token) throws JOSEException, BadJOSEException, ParseException {
        final Date expiration = getClaimsFromToken(token).getExpirationTime();
        return expiration.before(new Date());
    }
    
    @SuppressWarnings("unchecked")
	public static String refreshToken(String accessToken, String refreshToken) throws JOSEException, BadJOSEException, ParseException, PreconditionFailedException {
    	String refreshedToken="";
    	if(!isTokenExpired(refreshToken)) {
    		JWTClaimsSet claims = getClaimsFromToken(accessToken);
    		JWTClaimsSet refreshClaims = getClaimsFromToken(refreshToken);
    		if(claims.getSubject().equals(refreshClaims.getSubject())) {
    			refreshedToken = createAccessToken(claims.getSubject(), claims.getIssuer(), (List<String>) claims.getClaim("roles"));
    		}
    	}else {
    		throw new PreconditionFailedException("refreshToken", "expired");
    	}
    	return refreshedToken;
    }
    
}
