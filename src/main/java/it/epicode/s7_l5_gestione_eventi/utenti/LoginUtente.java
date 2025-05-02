package it.epicode.s7_l5_gestione_eventi.utenti;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginUtente {

    @NotBlank(message = "L'username è obbligatorio")
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    private String password;
}
