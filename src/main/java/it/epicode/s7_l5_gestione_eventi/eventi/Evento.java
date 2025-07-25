package it.epicode.s7_l5_gestione_eventi.eventi;


import it.epicode.s7_l5_gestione_eventi.utenti.Utente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "eventi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // O GenerationType.IDENTITY se preferisci
    private Long id;

    @Column(nullable = false, length = 100) // Assicurati che questi vincoli siano presenti
    private String titolo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descrizione;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, length = 100)
    private String luogo;

    @Column(nullable = false)
    private double prezzo;

    @Column(nullable = false)
    private int postiDisponibili;

    @ManyToOne
    @JoinColumn(name = "organizzatore_id", nullable = false)
    private Utente organizzatore;
}