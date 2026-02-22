package it.hackhub.application.mappers;

import it.hackhub.application.dto.auth.RegistrazioneDTO;
import it.hackhub.application.dto.utente.UtenteResponseDTO;
import it.hackhub.core.entities.core.Utente;
import java.time.LocalDate;

/**
 * Mapper tra Utente, RegistrazioneDTO e UtenteResponseDTO.
 */
public final class UtenteDtoMapper {

  private UtenteDtoMapper() {}

  /**
   * Converte RegistrazioneDTO in entità Utente (password in chiaro; va encodata nell'handler).
   */
  public static Utente toEntity(RegistrazioneDTO dto) {
    Utente utente = new Utente();
    utente.setEmail(dto.getEmail());
    utente.setPasswordHash(dto.getPassword());
    utente.setNome(dto.getNome());
    utente.setCognome(dto.getCognome());
    if (dto.getRuolo() == null || dto.getRuolo().isBlank()) {
      utente.setRuolo(Utente.RuoloStaff.AUTENTICATO);
    } else {
      String ruoloNorm = dto.getRuolo().trim().toUpperCase();
      try {
        utente.setRuolo(Utente.RuoloStaff.valueOf(ruoloNorm));
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException(
          "Ruolo non valido. Valori ammessi: AUTENTICATO, ORGANIZZATORE, MENTORE, GIUDICE"
        );
      }
    }
    return utente;
  }

  /**
   * Converte Utente in UtenteResponseDTO (senza password).
   */
  public static UtenteResponseDTO toResponseDTO(Utente utente) {
    UtenteResponseDTO dto = new UtenteResponseDTO();
    dto.setId(utente.getId());
    dto.setNome(utente.getNome());
    dto.setCognome(utente.getCognome());
    dto.setEmail(utente.getEmail());
    dto.setDataRegistrazione(utente.getDataRegistrazione());
    dto.setRuolo(
      utente.getRuolo() != null ? utente.getRuolo() : Utente.RuoloStaff.AUTENTICATO
    );
    return dto;
  }
}
