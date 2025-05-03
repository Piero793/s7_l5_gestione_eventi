package it.epicode.s7_l5_gestione_eventi.prenotazioni;


import lombok.Data;

import java.time.LocalDate;

@Data
public class PrenotazioneResponse {

    private Long id;
    private Long utenteId;
    private Long eventoId;
    private LocalDate dataPrenotazione;
}
