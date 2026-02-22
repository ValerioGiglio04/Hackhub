package it.hackhub.application.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Implementazione PasswordEncoder con BCrypt (senza Spring).
 */
public class BcryptPasswordEncoder implements PasswordEncoder {

  @Override
  public String encode(String rawPassword) {
    if (rawPassword == null) {
      throw new IllegalArgumentException("Password non può essere null");
    }
    return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    if (rawPassword == null || encodedPassword == null) {
      return false;
    }
    return BCrypt.checkpw(rawPassword, encodedPassword);
  }
}
