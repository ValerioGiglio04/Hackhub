package it.hackhub.application.repositories.associations;

import it.hackhub.core.entities.associations.InvitoTeam;
import java.util.List;
import java.util.Optional;

public interface InvitoTeamRepository {

  InvitoTeam save(InvitoTeam invito);
  Optional<InvitoTeam> findById(Long id);
  Optional<InvitoTeam> findByIdWithDetails(Long id);
  List<InvitoTeam> findByUtenteInvitatoIdAndStato(Long utenteInvitatoId, InvitoTeam.StatoInvito stato);
  List<InvitoTeam> findByTeamIdAndStato(Long teamId, InvitoTeam.StatoInvito stato);
  Optional<InvitoTeam> findByTeamIdAndUtenteInvitatoIdAndStato(
    Long teamId,
    Long utenteInvitatoId,
    InvitoTeam.StatoInvito stato
  );
}
