package it.epicode.s7_l5_gestione_eventi.prenotazioni;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrenotazioneRequest {

    @NotNull(message = "L'ID dell'utente è obbligatorio")
    private Long utenteId;

    @NotNull(message = "L'ID dell'evento è obbligatorio")
    private Long eventoId;
}
