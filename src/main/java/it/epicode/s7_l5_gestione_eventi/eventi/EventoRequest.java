package it.epicode.s7_l5_gestione_eventi.eventi;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventoRequest {

    @NotBlank(message = "Il titolo è obbligatorio")
    private String titolo;

    @NotBlank(message = "La descrizione è obbligatoria")
    private String descrizione;

    @NotNull(message = "La data è obbligatoria")
    private LocalDateTime data;

    @NotBlank(message = "Il luogo è obbligatorio")
    private String luogo;

    @NotNull(message = "Il numero di posti disponibili è obbligatorio")
    @Min(value = 0, message = "Il numero di posti disponibili non può essere negativo")
    private int postiDisponibili;
}
