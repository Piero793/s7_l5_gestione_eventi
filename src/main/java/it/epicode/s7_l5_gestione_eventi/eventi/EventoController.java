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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EventoController.class);

    @Autowired
    private EventoService eventoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventoResponse> creaEvento(@RequestBody @Valid EventoRequest eventoDTO) {
        logger.info("Corpo della richiesta JSON ricevuto: {}", eventoDTO);
        EventoResponse eventoCreato = eventoService.creaEvento(eventoDTO);
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

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> modificaEvento(@PathVariable Long id, @RequestBody @Valid EventoRequest eventoDTO) {
        EventoResponse eventoAggiornato = eventoService.modificaEvento(id, eventoDTO);
        return new ResponseEntity<>(eventoAggiornato, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaEvento(@PathVariable Long id) {
        eventoService.eliminaEvento(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}