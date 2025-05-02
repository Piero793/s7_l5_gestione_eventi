package it.epicode.s7_l5_gestione_eventi.eventi;

import it.epicode.s7_l5_gestione_eventi.security.Exceptions;
import it.epicode.s7_l5_gestione_eventi.utenti.Utente;
import it.epicode.s7_l5_gestione_eventi.utenti.UtenteRepository;
import it.epicode.s7_l5_gestione_eventi.utenti.RuoloUtente;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        // Otteniamo l'utente autenticato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            Utente utenteAutenticato = utenteRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utente autenticato non trovato"));

            // Verifichiamo se l'utente autenticato ha il ruolo di ORGANIZZATORE_EVENTI
            if (utenteAutenticato.getRuolo() != RuoloUtente.ORGANIZZATORE_EVENTI) {
                throw new Exceptions.AutorizzazioneNegataException("Solo gli organizzatori di eventi possono creare eventi.");
            }
        } else {
            throw new Exceptions.AutorizzazioneNegataException("Utente non autenticato o informazioni sull'utente non disponibili.");
        }

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

        // Otteniamo l'utente autenticato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            Utente utenteAutenticato = utenteRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utente autenticato non trovato"));

            // Verifichiamo se l'utente autenticato ha il ruolo di ORGANIZZATORE_EVENTI
            if (utenteAutenticato.getRuolo() != RuoloUtente.ORGANIZZATORE_EVENTI) {
                throw new Exceptions.AutorizzazioneNegataException("Solo gli organizzatori di eventi possono modificare gli eventi.");
            }

            Utente organizzatore = utenteRepository.findById(organizzatoreId)
                    .orElseThrow(() -> new RuntimeException("Organizzatore non trovato con ID: " + organizzatoreId));

            if (!eventoEsistente.getOrganizzatore().getId().equals(organizzatoreId)) {
                throw new Exceptions.AutorizzazioneNegataException("Non sei autorizzato a modificare questo evento.");
            }

            try {
                BeanUtils.copyProperties(eventoEsistente, eventoDTO);
            } catch (Exception e) {
                throw new RuntimeException("Errore durante la copia delle proprietà dell'evento", e);
            }
            eventoEsistente.setOrganizzatore(organizzatore);
            Evento eventoAggiornato = eventoRepository.save(eventoEsistente);
            return convertToResponseDTO(eventoAggiornato);

        } else {
            throw new Exceptions.AutorizzazioneNegataException("Utente non autenticato o informazioni sull'utente non disponibili.");
        }
    }

    public void eliminaEvento(Long id, Long organizzatoreId) {
        Evento eventoEsistente = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento non trovato con ID: " + id));

        // Otteniamo l'utente autenticato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            Utente utenteAutenticato = utenteRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utente autenticato non trovato"));

            // Verifichiamo se l'utente autenticato ha il ruolo di ORGANIZZATORE_EVENTI
            if (utenteAutenticato.getRuolo() != RuoloUtente.ORGANIZZATORE_EVENTI) {
                throw new Exceptions.AutorizzazioneNegataException("Solo gli organizzatori di eventi possono eliminare gli eventi.");
            }

            if (!eventoEsistente.getOrganizzatore().getId().equals(organizzatoreId)) {
                throw new Exceptions.AutorizzazioneNegataException("Non sei autorizzato a eliminare questo evento.");
            }

            eventoRepository.deleteById(id);

        } else {
            throw new Exceptions.AutorizzazioneNegataException("Utente non autenticato o informazioni sull'utente non disponibili.");
        }
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