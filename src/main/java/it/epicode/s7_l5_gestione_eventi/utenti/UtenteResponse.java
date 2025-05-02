package it.epicode.s7_l5_gestione_eventi.utenti;


import lombok.Data;

@Data
public class UtenteResponse {

    private Long id;
    private String username;
    private String ruolo;
}
