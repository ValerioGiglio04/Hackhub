package it.hackhub.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.security.userdetails.CustomUserDetails;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret:9a4f2c8d3b7a1e6f9a4f2c8d3b7a1e6f9a4f2c8d3b7a1e6f9a4f2c8d3b7a1e6f}")
  private String jwtSecret;

  @Value("${jwt.expiration:86400000}")
  private long jwtExpirationMs;

  private final TokenBlacklistService tokenBlacklistService;

  public JwtTokenProvider(TokenBlacklistService tokenBlacklistService) {
    this.tokenBlacklistService = tokenBlacklistService;
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Utente user = userDetails.getUtente();
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
    return Jwts.builder()
        .setSubject(user.getEmail())
        .claim("userId", user.getId())
        .claim("role", user.getRuolo() != null ? user.getRuolo().name() : "AUTENTICATO")
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUsernameFromJWT(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }

  public boolean validateToken(String authToken) {
    try {
      if (tokenBlacklistService.isTokenBlacklisted(authToken)) {
        return false;
      }
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public void invalidateToken(String token) {
    tokenBlacklistService.blacklistToken(token);
  }
}
