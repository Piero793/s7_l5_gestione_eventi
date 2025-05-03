package it.epicode.s7_l5_gestione_eventi.eventi;


import lombok.Data;

import java.time.LocalDate;

@Data
public class EventoResponse {
    private Long id;
    private String titolo;
    private String descrizione;
    private LocalDate data;
    private String luogo;
    private int postiDisponibili;
    private Long organizzatoreId;
}
