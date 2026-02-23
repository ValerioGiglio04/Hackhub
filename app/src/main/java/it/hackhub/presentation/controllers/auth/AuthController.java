package it.hackhub.presentation.controllers.auth;

import it.hackhub.application.dto.auth.LoginDTO;
import it.hackhub.application.dto.auth.RegistrazioneDTO;
import it.hackhub.application.dto.common.StandardResponse;
import it.hackhub.application.dto.utente.UtenteResponseDTO;
import it.hackhub.application.exceptions.core.EntityNotFoundException;
import it.hackhub.application.handlers.auth.AuthHandler;
import it.hackhub.application.mappers.UtenteDtoMapper;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.infrastructure.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autenticazione")
public class AuthController {

  private final AuthHandler authHandler;
  private final UtenteRepository utenteRepository;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthController(
    AuthHandler authHandler,
    UtenteRepository utenteRepository,
    JwtTokenProvider jwtTokenProvider
  ) {
    this.authHandler = authHandler;
    this.utenteRepository = utenteRepository;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PostMapping("/registrazione")
  public StandardResponse<UtenteResponseDTO> registrazione(
    @Valid @RequestBody RegistrazioneDTO dto
  ) {
    Utente utente = UtenteDtoMapper.toEntity(dto);
    Utente creato = authHandler.registrazione(utente);
    UtenteResponseDTO responseDto = UtenteDtoMapper.toResponseDTO(creato);
    return StandardResponse.success(responseDto);
  }

  @PostMapping("/login")
  public StandardResponse<Map<String, String>> login(
    @Valid @RequestBody LoginDTO dto
  ) {
    String token = authHandler.login(dto.getEmail(), dto.getPassword());
    return StandardResponse.success(Map.of("token", token));
  }

  @GetMapping("/me")
  public UtenteResponseDTO getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Utente utente = utenteRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new EntityNotFoundException("Utente con email " + email + " non trovato")
      );
    return UtenteDtoMapper.toResponseDTO(utente);
  }

  @PostMapping("/logout")
  public StandardResponse<Void> logout(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      String token = bearerToken.substring(7);
      jwtTokenProvider.invalidateToken(token);
    }
    return StandardResponse.success("Logout completato - token invalidato");
  }
}
