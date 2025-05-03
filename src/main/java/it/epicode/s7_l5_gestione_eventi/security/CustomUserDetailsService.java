package it.epicode.s7_l5_gestione_eventi.security;


import it.epicode.s7_l5_gestione_eventi.utenti.Utente;
import it.epicode.s7_l5_gestione_eventi.utenti.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con username: " + username));

        // ottengo il ruolo dell'utente, bisogno aggiungere il prefisso "ROLE_"
        String ruoloConPrefisso = utente.getRuolo().toString();
        if (!ruoloConPrefisso.startsWith("ROLE_")) {
            ruoloConPrefisso = "ROLE_" + ruoloConPrefisso;
        }

        // Crea una lista contenente un singolo SimpleGrantedAuthority con il ruolo prefissato
        return new User(utente.getUsername(), utente.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(ruoloConPrefisso)));
    }
}