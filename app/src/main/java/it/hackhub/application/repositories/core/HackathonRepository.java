package it.hackhub.application.repositories.core;

import it.hackhub.core.entities.core.Hackathon;
import it.hackhub.core.entities.core.StatoHackathon;
import java.util.List;
import java.util.Optional;

public interface HackathonRepository {

    Hackathon save(Hackathon hackathon);
    Optional<Hackathon> findById(Long id);
    List<Hackathon> findAll();
    List<Hackathon> findByStato(StatoHackathon stato);
}
