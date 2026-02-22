package it.hackhub.application.security;

/**
 * Interfaccia per encoding e verifica password (senza dipendenze da Spring).
 */
public interface PasswordEncoder {

  /**
   * Codifica la password in chiaro in hash.
   */
  String encode(String rawPassword);

  /**
   * Verifica se la password in chiaro corrisponde all'hash.
   */
  boolean matches(String rawPassword, String encodedPassword);
}
