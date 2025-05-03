package it.epicode.s7_l5_gestione_eventi.utenti;


import it.epicode.s7_l5_gestione_eventi.security.Exceptions;
import it.epicode.s7_l5_gestione_eventi.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/registrazione")
    public ResponseEntity<UtenteResponse> registraUtente(@RequestBody @Valid RegistrazioneUtente registrazione) {
        UtenteResponse utenteRegistrato = utenteService.registraUtente(registrazione);
        return new ResponseEntity<>(utenteRegistrato, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginUtente loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            throw new Exceptions.CredenzialiNonValideException("Credenziali non valide");
        } catch (Exception e) {
            System.out.println("Credenziali non valide"+ e);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}