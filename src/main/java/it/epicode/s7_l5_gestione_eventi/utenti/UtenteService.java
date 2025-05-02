package it.epicode.s7_l5_gestione_eventi.utenti;

import it.epicode.s7_l5_gestione_eventi.security.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UtenteResponse registraUtente(RegistrazioneUtente registrazione) {
        if (utenteRepository.existsByUsername(registrazione.getUsername())) {
            // Lanciamo la nostra eccezione personalizzata
            throw new Exceptions.UtenteGiaEsistenteException("Username già in uso");
        }

        Utente nuovoUtente = new Utente();
        nuovoUtente.setUsername(registrazione.getUsername());
        nuovoUtente.setPassword(registrazione.getPassword());

        // criptiamo la password
        nuovoUtente.setPassword(passwordEncoder.encode(nuovoUtente.getPassword()));

        // Convertiamo la stringa del ruolo in enum
        try {
            nuovoUtente.setRuolo(RuoloUtente.valueOf(registrazione.getRuolo().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ruolo non valido: " + registrazione.getRuolo());
        }

        Utente utenteSalvato = utenteRepository.save(nuovoUtente);

        UtenteResponse risposta = new UtenteResponse();
        risposta.setId(utenteSalvato.getId());
        risposta.setUsername(utenteSalvato.getUsername());
        risposta.setRuolo(utenteSalvato.getRuolo().toString());

        return risposta;
    }
}