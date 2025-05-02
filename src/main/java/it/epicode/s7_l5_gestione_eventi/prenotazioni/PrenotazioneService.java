package it.epicode.s7_l5_gestione_eventi.prenotazioni;


import it.epicode.s7_l5_gestione_eventi.eventi.Evento;
import it.epicode.s7_l5_gestione_eventi.eventi.EventoRepository;
import it.epicode.s7_l5_gestione_eventi.utenti.Utente;
import it.epicode.s7_l5_gestione_eventi.utenti.UtenteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenotazioneService {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    public PrenotazioneResponse prenotaPosto(PrenotazioneRequest prenotazioneDTO) {
        Utente utente = utenteRepository.findById(prenotazioneDTO.getUtenteId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + prenotazioneDTO.getUtenteId()));

        Evento evento = eventoRepository.findById(prenotazioneDTO.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento non trovato con ID: " + prenotazioneDTO.getEventoId()));

        if (prenotazioneRepository.existsByUtenteIdAndEventoId(utente.getId(), evento.getId())) {
            throw new RuntimeException("Hai già prenotato un posto per questo evento");
        }

        if (evento.getPostiDisponibili() <= 0) {
            throw new RuntimeException("Non ci sono più posti disponibili per questo evento");
        }

        Prenotazione nuovaPrenotazione = new Prenotazione();
        nuovaPrenotazione.setUtente(utente);
        nuovaPrenotazione.setEvento(evento);

        Prenotazione prenotazioneSalvata = prenotazioneRepository.save(nuovaPrenotazione);

        // Decrementa il numero di posti disponibili per l'evento
        evento.setPostiDisponibili(evento.getPostiDisponibili() - 1);
        eventoRepository.save(evento);

        PrenotazioneResponse risposta = new PrenotazioneResponse();
        try {
            BeanUtils.copyProperties(risposta, prenotazioneSalvata);
            risposta.setUtenteId(prenotazioneSalvata.getUtente().getId());
            risposta.setEventoId(prenotazioneSalvata.getEvento().getId());
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la copia delle proprietà della prenotazione", e);
        }

        return risposta;
    }

    public List<PrenotazioneResponse> getPrenotazioniUtente(Long utenteId) {
        utenteRepository.findById(utenteId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + utenteId));

        List<Prenotazione> prenotazioni = prenotazioneRepository.findByUtenteId(utenteId);
        return prenotazioni.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Metodo per annullare una prenotazione (EXTRA)
    public void annullaPrenotazione(Long id, Long utenteId) {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata con ID: " + id));

        if (!prenotazione.getUtente().getId().equals(utenteId)) {
            throw new RuntimeException("Non sei autorizzato ad annullare questa prenotazione");
        }

        Evento evento = prenotazione.getEvento();
        evento.setPostiDisponibili(evento.getPostiDisponibili() + 1);
        eventoRepository.save(evento);

        prenotazioneRepository.deleteById(id);
    }

    private PrenotazioneResponse convertToResponseDTO(Prenotazione prenotazione) {
        PrenotazioneResponse response = new PrenotazioneResponse();
        try {
            BeanUtils.copyProperties(response, prenotazione);
            response.setUtenteId(prenotazione.getUtente().getId());
            response.setEventoId(prenotazione.getEvento().getId());
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la copia delle proprietà della prenotazione", e);
        }
        return response;
    }
}
