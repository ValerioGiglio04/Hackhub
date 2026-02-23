package it.hackhub.infrastructure.security.jwt;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

  private final ConcurrentHashMap<String, LocalDateTime> blacklist = new ConcurrentHashMap<>();

  public void blacklistToken(String token) {
    blacklist.put(token, LocalDateTime.now());
  }

  public boolean isTokenBlacklisted(String token) {
    return blacklist.containsKey(token);
  }
}
