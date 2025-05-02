package it.epicode.s7_l5_gestione_eventi.prenotazioni;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
    List<Prenotazione> findByUtenteId(Long utenteId);
    boolean existsByUtenteIdAndEventoId(Long utenteId, Long eventoId);
}
