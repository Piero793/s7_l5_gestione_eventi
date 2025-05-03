package it.epicode.s7_l5_gestione_eventi.eventi;


import it.epicode.s7_l5_gestione_eventi.security.Exceptions;
import it.epicode.s7_l5_gestione_eventi.utenti.Utente;
import it.epicode.s7_l5_gestione_eventi.utenti.UtenteRepository;
import it.epicode.s7_l5_gestione_eventi.utenti.RuoloUtente;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private static final Logger logger = LoggerFactory.getLogger(EventoService.class);

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    public EventoResponse creaEvento(EventoRequest eventoDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            Utente organizzatoreAutenticato = utenteRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("Utente autenticato non trovato per username: {}", username);
                        return new Exceptions.UtenteNonTrovatoException("Utente autenticato non trovato");
                    });
            logger.info("Organizzatore autenticato trovato: {}", organizzatoreAutenticato.getUsername());

            if (organizzatoreAutenticato.getRuolo() != RuoloUtente.ROLE_ORGANIZZATORE_EVENTI) {
                logger.warn("L'utente {} non è un organizzatore di eventi.", organizzatoreAutenticato.getUsername());
                throw new Exceptions.AutorizzazioneNegataException("Solo gli organizzatori di eventi possono creare eventi.");
            }

            Evento nuovoEvento = new Evento();
            nuovoEvento.setTitolo(eventoDTO.getTitolo());
            nuovoEvento.setDescrizione(eventoDTO.getDescrizione());
            nuovoEvento.setData(eventoDTO.getData());
            nuovoEvento.setLuogo(eventoDTO.getLuogo());
            nuovoEvento.setPostiDisponibili(eventoDTO.getPostiDisponibili());
            nuovoEvento.setOrganizzatore(organizzatoreAutenticato);
            logger.info("Nuovo evento creato con titolo: {}", nuovoEvento.getTitolo());

            Evento eventoSalvato = eventoRepository.save(nuovoEvento);
            logger.info("Evento salvato con ID: {}", eventoSalvato.getId());
            return convertToResponseDTO(eventoSalvato);

        } else {
            logger.warn("Tentativo di creare un evento da utente non autenticato.");
            throw new Exceptions.AutorizzazioneNegataException("Utente non autenticato o informazioni sull'utente non disponibili.");
        }
    }

    public List<EventoResponse> getAllEventi() {
        logger.info("Recupero di tutti gli eventi.");
        List<Evento> eventi = eventoRepository.findAll();
        logger.info("Trovati {} eventi.", eventi.size());
        return eventi.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public EventoResponse getEventoById(Long id) {
        logger.info("Ricerca dell'evento con ID: {}", id);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Evento non trovato con ID: {}", id);
                    return new EntityNotFoundException("Evento non trovato con ID: " + id);
                });
        logger.info("Evento trovato con ID: {}", evento.getId());
        return convertToResponseDTO(evento);
    }

    public EventoResponse modificaEvento(Long id, EventoRequest eventoDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            Utente organizzatoreAutenticato = utenteRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("Utente autenticato non trovato per username: {}", username);
                        return new Exceptions.UtenteNonTrovatoException("Utente autenticato non trovato");
                    });
            logger.info("Organizzatore autenticato trovato: {}", organizzatoreAutenticato.getUsername());

            if (organizzatoreAutenticato.getRuolo() != RuoloUtente.ROLE_ORGANIZZATORE_EVENTI) {
                logger.warn("L'utente {} non è un organizzatore di eventi.", organizzatoreAutenticato.getUsername());
                throw new Exceptions.AutorizzazioneNegataException("Solo gli organizzatori di eventi possono modificare gli eventi.");
            }

            logger.info("Ricerca dell'evento da modificare con ID: {}", id);
            Evento eventoEsistente = eventoRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Evento non trovato con ID: {}", id);
                        return new EntityNotFoundException("Evento non trovato con ID: " + id);
                    });
            logger.info("Evento esistente trovato con ID: {}", eventoEsistente.getId());

            if (!eventoEsistente.getOrganizzatore().getId().equals(organizzatoreAutenticato.getId())) {
                logger.warn("L'utente {} non è autorizzato a modificare l'evento con ID: {}", organizzatoreAutenticato.getUsername(), id);
                throw new Exceptions.AutorizzazioneNegataException("Non sei autorizzato a modificare questo evento.");
            }

            eventoEsistente.setTitolo(eventoDTO.getTitolo());
            eventoEsistente.setDescrizione(eventoDTO.getDescrizione());
            eventoEsistente.setData(eventoDTO.getData());
            eventoEsistente.setLuogo(eventoDTO.getLuogo());
            eventoEsistente.setPostiDisponibili(eventoDTO.getPostiDisponibili());
            logger.info("Evento con ID {} modificato.", id);

            Evento eventoAggiornato = eventoRepository.save(eventoEsistente);
            logger.info("Evento aggiornato con ID: {}", eventoAggiornato.getId());
            return convertToResponseDTO(eventoAggiornato);

        } else {
            logger.warn("Tentativo di modificare un evento da utente non autenticato.");
            throw new Exceptions.AutorizzazioneNegataException("Utente non autenticato o informazioni sull'utente non disponibili.");
        }
    }

    public void eliminaEvento(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            Utente organizzatoreAutenticato = utenteRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("Utente autenticato non trovato per username: {}", username);
                        return new Exceptions.UtenteNonTrovatoException("Utente autenticato non trovato");
                    });
            logger.info("Organizzatore autenticato trovato: {}", organizzatoreAutenticato.getUsername());

            if (organizzatoreAutenticato.getRuolo() != RuoloUtente.ROLE_ORGANIZZATORE_EVENTI) {
                logger.warn("L'utente {} non è un organizzatore di eventi.", organizzatoreAutenticato.getUsername());
                throw new Exceptions.AutorizzazioneNegataException("Solo gli organizzatori di eventi possono eliminare gli eventi.");
            }

            logger.info("Ricerca dell'evento da eliminare con ID: {}", id);
            Evento eventoEsistente = eventoRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Evento non trovato con ID: {}", id);
                        return new EntityNotFoundException("Evento non trovato con ID: " + id);
                    });
            logger.info("Evento esistente trovato con ID: {}", eventoEsistente.getId());

            if (!eventoEsistente.getOrganizzatore().getId().equals(organizzatoreAutenticato.getId())) {
                logger.warn("L'utente {} non è autorizzato a eliminare l'evento con ID: {}", organizzatoreAutenticato.getUsername(), id);
                throw new Exceptions.AutorizzazioneNegataException("Non sei autorizzato a eliminare questo evento.");
            }

            eventoRepository.deleteById(id);
            logger.info("Evento con ID {} eliminato.", id);

        } else {
            logger.warn("Tentativo di eliminare un evento da utente non autenticato.");
            throw new Exceptions.AutorizzazioneNegataException("Utente non autenticato o informazioni sull'utente non disponibili.");
        }
    }

    private EventoResponse convertToResponseDTO(Evento evento) {
        EventoResponse response = new EventoResponse();
        response.setId(evento.getId());
        response.setTitolo(evento.getTitolo());
        response.setDescrizione(evento.getDescrizione());
        response.setData(evento.getData());
        response.setLuogo(evento.getLuogo());
        response.setPostiDisponibili(evento.getPostiDisponibili());
        response.setOrganizzatoreId(evento.getOrganizzatore().getId());
        logger.debug("Convertito evento con ID: {} in EventoResponse con titolo: {}", evento.getId(), evento.getTitolo());
        return response;
    }
}