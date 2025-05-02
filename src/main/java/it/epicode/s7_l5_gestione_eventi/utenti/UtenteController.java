package it.epicode.s7_l5_gestione_eventi.utenti;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @PostMapping("/registrazione")
    public ResponseEntity<UtenteResponse> registraUtente(@RequestBody @Valid RegistrazioneUtenteDTO registrazioneDTO) {
        UtenteResponse utenteRegistrato = utenteService.registraUtente(registrazioneDTO);
        return new ResponseEntity<>(utenteRegistrato, HttpStatus.CREATED);
    }
}
