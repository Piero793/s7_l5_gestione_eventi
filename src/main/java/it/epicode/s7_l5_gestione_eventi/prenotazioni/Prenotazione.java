package it.epicode.s7_l5_gestione_eventi.prenotazioni;


import it.epicode.s7_l5_gestione_eventi.eventi.Evento;
import it.epicode.s7_l5_gestione_eventi.utenti.Utente;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "prenotazioni")
@Data
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private Utente utente;

    @ManyToOne
    private Evento evento;

    @Column(nullable = false)
    private LocalDateTime dataPrenotazione;

    public Prenotazione() {
        this.dataPrenotazione = LocalDateTime.now();
    }
}
