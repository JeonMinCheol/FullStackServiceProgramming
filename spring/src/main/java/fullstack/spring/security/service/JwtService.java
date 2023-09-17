package fullstack.spring.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(@NonNull String email) throws NoSuchAlgorithmException, NoSuchProviderException {
        Claims claim = Jwts.claims().setSubject(email);

        return Jwts
                .builder()
                .setIssuedAt(new Date())
                .setClaims(claim)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Claims extractAllClaims(@NonNull String jwt) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public Optional<String> extractToken(@NonNull String jwt) {
        return jwt.startsWith("Bearer ") ? Optional.of(jwt.substring(7)) : Optional.empty();
    }

    public String extractEmail(@NonNull String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    public Authentication getAuthentication(@NonNull UserDetails userDetails) throws Exception {
        try{
            String email = userDetails.getUsername();
            String password = userDetails.getPassword();

            return new UsernamePasswordAuthenticationToken(email, password, userDetails.getAuthorities());
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
