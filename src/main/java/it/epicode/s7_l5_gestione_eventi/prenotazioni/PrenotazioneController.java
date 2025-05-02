package it.epicode.s7_l5_gestione_eventi.prenotazioni;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {

    @Autowired
    private PrenotazioneService prenotazioneService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PrenotazioneResponse> prenotaPosto(@RequestBody @Valid PrenotazioneRequest prenotazione) {
        PrenotazioneResponse prenotazioneCreata = prenotazioneService.prenotaPosto(prenotazione);
        return new ResponseEntity<>(prenotazioneCreata , HttpStatus.CREATED);
    }

    @GetMapping("/utenti/{utenteId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<PrenotazioneResponse>> getPrenotazioniUtente(@PathVariable Long utenteId) {
        List<PrenotazioneResponse> prenotazioni = prenotazioneService.getPrenotazioniUtente(utenteId);
        return new ResponseEntity<>(prenotazioni , HttpStatus.OK);
    }

    // Endpoint per annullare una prenotazione (EXTRA DEL PROGETTO)
    @DeleteMapping("/{id}/utenti/{utenteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> annullaPrenotazione(@PathVariable Long id, @PathVariable Long utenteId) {
        prenotazioneService.annullaPrenotazione(id, utenteId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
