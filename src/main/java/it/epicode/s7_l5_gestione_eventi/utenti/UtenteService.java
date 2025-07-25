package it.epicode.s7_l5_gestione_eventi.utenti;

import it.epicode.s7_l5_gestione_eventi.security.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtenteService {

    private static final Logger logger = LoggerFactory.getLogger(UtenteService.class); // Inizializza il logger

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UtenteResponse registraUtente(RegistrazioneUtente registrazione) {
        logger.info("Tentativo di registrazione per username: {}", registrazione.getUsername());

        if (utenteRepository.existsByUsername(registrazione.getUsername())) {
            // eccezione personalizzata
            logger.warn("Tentativo di registrazione con username già in uso: {}", registrazione.getUsername());
            throw new Exceptions.UtenteGiaEsistenteException("Username già in uso");
        }

        Utente nuovoUtente = new Utente();
        nuovoUtente.setUsername(registrazione.getUsername());

        // Cripto la password direttamente dal DTO
        nuovoUtente.setPassword(passwordEncoder.encode(registrazione.getPassword()));

        // Assegna il ruolo predefinito (ROLE_UTENTE) manualmente, senza prenderlo dal DTO
        nuovoUtente.setRuolo(RuoloUtente.ROLE_UTENTE); // <--- MODIFICATO QUI

        logger.info("Assegnato ruolo predefinito {} all'utente {}", nuovoUtente.getRuolo(), nuovoUtente.getUsername());

        Utente utenteSalvato = utenteRepository.save(nuovoUtente);
        logger.info("Utente salvato nel database con ID: {}", utenteSalvato.getId());

        UtenteResponse risposta = new UtenteResponse();
        risposta.setId(utenteSalvato.getId());
        risposta.setUsername(utenteSalvato.getUsername());
        risposta.setRuolo(utenteSalvato.getRuolo().toString()); // Ottieni il ruolo dall'oggetto utente salvato

        logger.info("Registrazione utente completata per {}", risposta.getUsername());
        return risposta;
    }
}