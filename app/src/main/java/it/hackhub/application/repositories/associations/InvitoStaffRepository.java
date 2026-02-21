package it.hackhub.application.repositories.associations;

import it.hackhub.core.entities.associations.InvitoStaff;
import java.util.List;
import java.util.Optional;

public interface InvitoStaffRepository {

  InvitoStaff save(InvitoStaff invito);
  Optional<InvitoStaff> findById(Long id);
  Optional<InvitoStaff> findByIdWithDetails(Long id);
  List<InvitoStaff> findByUtenteInvitatoIdAndStato(Long utenteInvitatoId, InvitoStaff.StatoInvito stato);
  List<InvitoStaff> findByHackathonIdAndStato(Long hackathonId, InvitoStaff.StatoInvito stato);
}
