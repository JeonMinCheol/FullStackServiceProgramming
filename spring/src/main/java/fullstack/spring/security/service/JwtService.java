package fullstack.spring.security.service;

import fullstack.spring.repository.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private UserRepo userRepo;

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

    public Optional<String> extractTokenFromHeader(HttpServletRequest request) throws IOException, ServletException {
        return Optional.ofNullable(request.getHeader("Authorization")).filter(accessToken -> accessToken.startsWith("Bearer ")).map(accessToken -> accessToken.replace("Bearer ", ""));
    }

    public long extractIdFromHeader(HttpServletRequest request) throws IOException, ServletException {
        Long id = null;

        try{
            Optional<String> token = extractTokenFromHeader(request);
            if(token.isPresent()) {
                id = userRepo.findByEmail(tokenToEmail(token.get())).get().getId();
                return id;
            }

            throw new ServletException("Token not valid.");
        } catch (IOException e) {
            throw new IOException(e);
        } catch (ServletException e) {
            throw new ServletException(e);
        }
    }

    public String extractEmailFromHeader(HttpServletRequest request) throws IOException, ServletException {
        String userEmail = null;

        try{
            Optional<String> token = extractTokenFromHeader(request);
            if(token.isPresent()) {
                userEmail = tokenToEmail(token.get());
                return userEmail;
            }

            throw new ServletException("Token not valid.");
        } catch (IOException e) {
            throw new IOException(e);
        } catch (ServletException e) {
            throw new ServletException(e);
        }
    }

    public Optional<String> extractToken(@NonNull String jwt) {
        return jwt.startsWith("Bearer ") ? Optional.of(jwt.substring(7)) : Optional.empty();
    }

    public String tokenToEmail(@NonNull String jwt) {
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
