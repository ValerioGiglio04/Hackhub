package it.hackhub.application.security.jwt;

import it.hackhub.core.entities.core.Utente;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 * Generazione e validazione token JWT (senza dipendenze da Spring).
 */
public class JwtTokenProvider {

  private static final int MIN_SECRET_LENGTH = 32;

  private final SecretKey signingKey;
  private final long expirationMs;

  /**
   * @param secretBase64OrRaw secret per HMAC (almeno 32 caratteri per HS256)
   * @param expirationMs validità del token in millisecondi
   */
  public JwtTokenProvider(String secretBase64OrRaw, long expirationMs) {
    byte[] keyBytes = secretBase64OrRaw.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < MIN_SECRET_LENGTH) {
      throw new IllegalArgumentException("JWT secret deve essere di almeno " + MIN_SECRET_LENGTH + " byte");
    }
    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    this.expirationMs = expirationMs;
  }

  /**
   * Genera un token JWT per l'utente autenticato.
   */
  public String generateToken(Utente user) {
    if (user == null || user.getEmail() == null) {
      throw new IllegalArgumentException("Utente e email obbligatori");
    }
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);
    return Jwts.builder()
        .subject(user.getEmail())
        .claim("userId", user.getId())
        .claim("role", user.getRuolo() != null ? user.getRuolo().name() : "AUTENTICATO")
        .issuedAt(now)
        .expiration(expiry)
        .signWith(signingKey)
        .compact();
  }

  public String getUsernameFromToken(String token) {
    return parseClaims(token).getSubject();
  }

  public Long getUserIdFromToken(String token) {
    return parseClaims(token).get("userId", Long.class);
  }

  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
