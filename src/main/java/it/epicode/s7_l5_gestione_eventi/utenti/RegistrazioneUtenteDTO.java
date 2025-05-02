package it.epicode.s7_l5_gestione_eventi.utenti;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrazioneUtenteDTO {

    @NotBlank(message = "L'username è obbligatorio")
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 6, message = "La password deve contenere almeno 6 caratteri")
    private String password;

    @NotBlank(message = "Il ruolo è obbligatorio")
    private String ruolo;
}
