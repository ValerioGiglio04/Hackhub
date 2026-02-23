package it.hackhub.application.repositories.support;

import it.hackhub.core.entities.support.SegnalazioneViolazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegnalazioneViolazioneRepository extends JpaRepository<SegnalazioneViolazione, Long> {}
