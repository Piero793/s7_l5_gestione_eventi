package it.epicode.s7_l5_gestione_eventi.utenti;

import it.epicode.s7_l5_gestione_eventi.eventi.Evento;
import it.epicode.s7_l5_gestione_eventi.prenotazioni.Prenotazione;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "utenti")
@Data
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RuoloUtente ruolo;

    @OneToMany(mappedBy = "organizzatore")
    private List<Evento> eventiCreati;

    @OneToMany(mappedBy = "utente")
    private List<Prenotazione> prenotazioni;
}
