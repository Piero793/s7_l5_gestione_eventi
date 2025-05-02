package it.epicode.s7_l5_gestione_eventi.eventi;

import it.epicode.s7_l5_gestione_eventi.utenti.Utente;
import it.epicode.s7_l5_gestione_eventi.utenti.UtenteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    public EventoResponse creaEvento(EventoRequest eventoDTO, Long organizzatoreId) {
        Utente organizzatore = utenteRepository.findById(organizzatoreId)
                .orElseThrow(() -> new RuntimeException("Organizzatore non trovato con ID: " + organizzatoreId));

        Evento nuovoEvento = new Evento();
        try {
            BeanUtils.copyProperties(nuovoEvento, eventoDTO);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la copia delle proprietà dell'evento", e);
        }
        nuovoEvento.setOrganizzatore(organizzatore);

        Evento eventoSalvato = eventoRepository.save(nuovoEvento);

        EventoResponse risposta = new EventoResponse();
        try {
            BeanUtils.copyProperties(risposta, eventoSalvato);
            risposta.setOrganizzatoreId(eventoSalvato.getOrganizzatore().getId());
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la copia delle proprietà dell'evento", e);
        }

        return risposta;
    }

    public List<EventoResponse> getAllEventi() {
        List<Evento> eventi = eventoRepository.findAll();
        return eventi.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public EventoResponse getEventoById(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato con ID: " + id));
        return convertToResponseDTO(evento);
    }

    public EventoResponse modificaEvento(Long id, EventoRequest eventoDTO, Long organizzatoreId) {
        Evento eventoEsistente = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato con ID: " + id));

        Utente organizzatore = utenteRepository.findById(organizzatoreId)
                .orElseThrow(() -> new RuntimeException("Organizzatore non trovato con ID: " + organizzatoreId));

        if (!eventoEsistente.getOrganizzatore().getId().equals(organizzatoreId)) {
            throw new RuntimeException("Non sei autorizzato a modificare questo evento");
        }

        try {
            BeanUtils.copyProperties(eventoEsistente, eventoDTO);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la copia delle proprietà dell'evento", e);
        }
        eventoEsistente.setOrganizzatore(organizzatore);
        Evento eventoAggiornato = eventoRepository.save(eventoEsistente);
        return convertToResponseDTO(eventoAggiornato);
    }

    public void eliminaEvento(Long id, Long organizzatoreId) {
        Evento eventoEsistente = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato con ID: " + id));

        if (!eventoEsistente.getOrganizzatore().getId().equals(organizzatoreId)) {
            throw new RuntimeException("Non sei autorizzato a eliminare questo evento");
        }

        eventoRepository.deleteById(id);
    }

    private EventoResponse convertToResponseDTO(Evento evento) {
        EventoResponse response = new EventoResponse();
        try {
            BeanUtils.copyProperties(response, evento);
            response.setOrganizzatoreId(evento.getOrganizzatore().getId());
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la copia delle proprietà dell'evento", e);
        }
        return response;
    }
}
