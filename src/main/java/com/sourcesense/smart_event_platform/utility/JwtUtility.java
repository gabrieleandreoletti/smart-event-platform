package com.sourcesense.smart_event_platform.utility;

import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.configuration.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtility {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long EXPIRATION = 1000 * 60 * 60 * 24;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Customer customer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", List.of("ROLE_" + customer.getRole().toString()));

        return Jwts.builder()
                .claims(claims)
                .subject(customer.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSecretKey())
                .compact();
    }

    public Claims getPayload(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }

    public String getUsername(String token) {
        return getPayload(token).getSubject();
    }

    public Date getExpiration(String token) throws SignatureException {
        return getPayload(token).getExpiration();
    }

    public Role getRole(String token) {
        return (Role) getPayload(token).get("role");
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return !isTokenExpired(token) && isSubjectValid(token, userDetails) && isUserValid(userDetails);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    private boolean isSubjectValid(String token, UserDetails userDetails) {
        return getUsername(token).equals(userDetails.getUsername());
    }

    private boolean isUserValid(UserDetails userDetails) {
        return userDetails.isEnabled() && userDetails.isAccountNonExpired() && userDetails.isCredentialsNonExpired();
    }

}
