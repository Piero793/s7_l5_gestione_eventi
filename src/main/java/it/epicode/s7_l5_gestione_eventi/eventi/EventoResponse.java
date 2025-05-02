package it.epicode.s7_l5_gestione_eventi.eventi;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventoResponse {

    private Long id;
    private String titolo;
    private String descrizione;
    private LocalDateTime data;
    private String luogo;
    private int postiDisponibili;
    private Long organizzatoreId;
}
