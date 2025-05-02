package it.epicode.s7_l5_gestione_eventi.eventi;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventi")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping("/organizzatori/{organizzatoreId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventoResponse> creaEvento(@PathVariable Long organizzatoreId, @RequestBody @Valid EventoRequest eventoDTO) {
        EventoResponse eventoCreato = eventoService.creaEvento(eventoDTO, organizzatoreId);
        return new ResponseEntity<>(eventoCreato, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> getAllEventi() {
        List<EventoResponse> eventi = eventoService.getAllEventi();
        return new ResponseEntity<>(eventi, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> getEventoById(@PathVariable Long id) {
        EventoResponse evento = eventoService.getEventoById(id);
        return new ResponseEntity<>(evento, HttpStatus.OK);
    }

    @PutMapping("/{id}/organizzatori/{organizzatoreId}")
    public ResponseEntity<EventoResponse> modificaEvento(@PathVariable Long id, @PathVariable Long organizzatoreId, @RequestBody @Valid EventoRequest eventoDTO) {
        EventoResponse eventoAggiornato = eventoService.modificaEvento(id, eventoDTO, organizzatoreId);
        return new ResponseEntity<>(eventoAggiornato, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/organizzatori/{organizzatoreId}")
    public ResponseEntity<Void> eliminaEvento(@PathVariable Long id, @PathVariable Long organizzatoreId) {
        eventoService.eliminaEvento(id, organizzatoreId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
