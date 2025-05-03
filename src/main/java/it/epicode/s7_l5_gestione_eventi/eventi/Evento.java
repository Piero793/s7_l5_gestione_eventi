package it.epicode.s7_l5_gestione_eventi.eventi;


import it.epicode.s7_l5_gestione_eventi.utenti.Utente;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "eventi")
@Data
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String titolo;

    private String descrizione;

    private LocalDate data;

    private String luogo;

    private int postiDisponibili;

    @ManyToOne
    private Utente organizzatore;
}