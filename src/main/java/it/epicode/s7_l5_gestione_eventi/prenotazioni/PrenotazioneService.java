package it.epicode.s7_l5_gestione_eventi.prenotazioni;


import it.epicode.s7_l5_gestione_eventi.eventi.Evento;
import it.epicode.s7_l5_gestione_eventi.eventi.EventoRepository;
import it.epicode.s7_l5_gestione_eventi.utenti.Utente;
import it.epicode.s7_l5_gestione_eventi.utenti.UtenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenotazioneService {

    private static final Logger logger = LoggerFactory.getLogger(PrenotazioneService.class);

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    public PrenotazioneResponse prenotaPosto(PrenotazioneRequest prenotazioneDTO) {
        logger.info("Tentativo di prenotazione per Utente ID: {} e Evento ID: {}", prenotazioneDTO.getUtenteId(), prenotazioneDTO.getEventoId());

        Utente utente = utenteRepository.findById(prenotazioneDTO.getUtenteId())
                .orElseThrow(() -> {
                    ;
                    return new RuntimeException("Utente non trovato con ID: " + prenotazioneDTO.getUtenteId());
                });
        logger.info("Utente trovato: {}", utente.getUsername());

        Evento evento = eventoRepository.findById(prenotazioneDTO.getEventoId())
                .orElseThrow(() -> {
                    logger.error("Evento non trovato con ID: {}", prenotazioneDTO.getEventoId());
                    return new RuntimeException("Evento non trovato con ID: " + prenotazioneDTO.getEventoId());
                });
        logger.info("Evento trovato: {}", evento.getTitolo());

        if (prenotazioneRepository.existsByUtenteIdAndEventoId(utente.getId(), evento.getId())) {
            logger.warn("L'utente {} ha già prenotato un posto per l'evento {}", utente.getUsername(), evento.getTitolo());
            throw new RuntimeException("Hai già prenotato un posto per questo evento");
        }

        if (evento.getPostiDisponibili() <= 0) {
            logger.warn("Non ci sono più posti disponibili per l'evento {}", evento.getTitolo());
            throw new RuntimeException("Non ci sono più posti disponibili per questo evento");
        }

        Prenotazione nuovaPrenotazione = new Prenotazione();
        nuovaPrenotazione.setUtente(utente);
        nuovaPrenotazione.setEvento(evento);
        nuovaPrenotazione.setDataPrenotazione(LocalDate.now());

        Prenotazione prenotazioneSalvata = prenotazioneRepository.save(nuovaPrenotazione);
        logger.info("Prenotazione salvata con ID: {}", prenotazioneSalvata.getId());

        // Decrementa il numero di posti disponibili per l'evento di 1
        evento.setPostiDisponibili(evento.getPostiDisponibili() - 1);
        eventoRepository.save(evento);
        logger.info("Posti disponibili per l'evento {} decrementati a: {}", evento.getTitolo(), evento.getPostiDisponibili());

        PrenotazioneResponse risposta = new PrenotazioneResponse();
        risposta.setId(prenotazioneSalvata.getId());
        risposta.setUtenteId(prenotazioneSalvata.getUtente().getId());
        risposta.setEventoId(prenotazioneSalvata.getEvento().getId());
        risposta.setDataPrenotazione(prenotazioneSalvata.getDataPrenotazione()); // Ora la data non sarà più null
        logger.info("Risposta di prenotazione creata con ID: {}, Utente ID: {}, Evento ID: {}, Data Prenotazione: {}",
                risposta.getId(),
                risposta.getUtenteId(),
                risposta.getEventoId(),
                risposta.getDataPrenotazione());

        return risposta;
    }
    public List<PrenotazioneResponse> getPrenotazioniUtente(Long utenteId) {
        logger.info("Ricerca delle prenotazioni per l'Utente ID: {}", utenteId);
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> {
                    logger.error("Utente non trovato con ID: {}", utenteId);
                    return new RuntimeException("Utente non trovato con ID: " + utenteId);
                });

        List<Prenotazione> prenotazioni = prenotazioneRepository.findByUtenteId(utenteId);
        logger.info("Trovate {} prenotazioni per l'Utente ID: {}", prenotazioni.size(), utenteId);
        return prenotazioni.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Metodo per annullare una prenotazione (EXTRA DEL PROGETTO)
    public void annullaPrenotazione(Long id, Long utenteId) {
        logger.info("Tentativo di annullamento della prenotazione con ID: {} per l'Utente ID: {}", id, utenteId);
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Prenotazione non trovata con ID: {}", id);
                    return new RuntimeException("Prenotazione non trovata con ID: " + id);
                });
        logger.info("Prenotazione trovata con Utente ID: {} ed Evento ID: {}", prenotazione.getUtente().getId(), prenotazione.getEvento().getId());

        if (!prenotazione.getUtente().getId().equals(utenteId)) {
            logger.warn("L'utente {} non è autorizzato ad annullare la prenotazione con ID: {}", utenteId, id);
            throw new RuntimeException("Non sei autorizzato ad annullare questa prenotazione");
        }

        Evento evento = prenotazione.getEvento();
        evento.setPostiDisponibili(evento.getPostiDisponibili() + 1);
        eventoRepository.save(evento);
        logger.info("Posti disponibili per l'evento {} incrementati a: {}", evento.getTitolo(), evento.getPostiDisponibili());

        prenotazioneRepository.deleteById(id);
        logger.info("Prenotazione con ID: {} annullata con successo", id);
    }

    private PrenotazioneResponse convertToResponseDTO(Prenotazione prenotazione) {
        PrenotazioneResponse response = new PrenotazioneResponse();
        response.setId(prenotazione.getId());
        response.setUtenteId(prenotazione.getUtente().getId());
        response.setEventoId(prenotazione.getEvento().getId());
        response.setDataPrenotazione(prenotazione.getDataPrenotazione());
        logger.debug("Convertita prenotazione con ID: {} in PrenotazioneResponse con Utente ID: {}, Evento ID: {} e Data Prenotazione: {}",
                prenotazione.getId(), response.getUtenteId(), response.getEventoId(), response.getDataPrenotazione());
        return response;
    }
}
