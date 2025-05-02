package it.epicode.s7_l5_gestione_eventi.utenti;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UtenteResponse registraUtente(RegistrazioneUtenteDTO registrazioneDTO) {
        if (utenteRepository.existsByUsername(registrazioneDTO.getUsername())) {
            throw new RuntimeException("Username già in uso");
        }

        Utente nuovoUtente = new Utente();
        try {
            BeanUtils.copyProperties(nuovoUtente, registrazioneDTO);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'impostazione di una proprietà dell'utente", e);
        }

        nuovoUtente.setPassword(passwordEncoder.encode(nuovoUtente.getPassword()));

        // Convertiamo la stringa del ruolo in enum
        try {
            nuovoUtente.setRuolo(RuoloUtente.valueOf(registrazioneDTO.getRuolo().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ruolo non valido: " + registrazioneDTO.getRuolo());
        }

        Utente utenteSalvato = utenteRepository.save(nuovoUtente);

        UtenteResponse risposta = new UtenteResponse();
        try {
            BeanUtils.copyProperties(risposta, utenteSalvato);
            risposta.setRuolo(utenteSalvato.getRuolo().toString());
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la copia delle proprietà dell'utente", e);
        }

        return risposta;
    }
}