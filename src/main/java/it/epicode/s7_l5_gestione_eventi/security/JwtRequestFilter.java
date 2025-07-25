package it.epicode.s7_l5_gestione_eventi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays; // Import aggiunto
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/registrazione",
            "/v3/api-docs",
            "/swagger-ui"

    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        logger.info("Processing request for URI: {}", requestUri);


        boolean isPublicEndpoint = PUBLIC_ENDPOINTS.stream().anyMatch(requestUri::startsWith);

        if (isPublicEndpoint) {
            logger.info("URI '{}' is a public endpoint. Bypassing JWT filter.", requestUri);
            filterChain.doFilter(request, response); // Passa la richiesta al prossimo filtro nella catena
            return; // Termina l'esecuzione di questo filtro per la richiesta corrente
        }

        // --- Inizio della logica di validazione JWT per le richieste non pubbliche ---

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        logger.info("Richiesta intercettata dal JwtRequestFilter");
        logger.info("Header Authorization: {}", authHeader);

        if (StringUtils.hasText(authHeader)) {
            if (authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                logger.info("JWT estratto (senza 'Bearer '): {}", jwt);
            } else {
                logger.warn("L'header Authorization non inizia con 'Bearer '");
                jwt = authHeader;
                logger.info("Assumendo che l'intero header sia il JWT: {}", jwt);
            }

            try {
                username = jwtUtil.extractUsername(jwt);
                logger.info("Username estratto dal JWT: {}", username);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                logger.error("Token JWT scaduto: {}", e.getMessage());
                SecurityContextHolder.clearContext(); // Pulisce il contesto di sicurezza
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                logger.error("Token JWT malformato: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (io.jsonwebtoken.SignatureException e) {
                logger.error("Firma del token JWT non valida: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (IllegalArgumentException e) {
                logger.error("Errore nell'elaborazione del token JWT: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Nessuna autenticazione presente nel SecurityContextHolder per l'utente: {}", username);


            if (jwtUtil.isTokenValid(jwt)) {
                logger.info("Il token JWT è valido");

                // Estrai i ruoli direttamente dal token JWT
                List<String> roles = jwtUtil.extractRoles(jwt);
                logger.info("Ruoli estratti dal token: {}", roles);

                // Converte i ruoli in SimpleGrantedAuthority
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                logger.info("Autorità create: {}", authorities);

                // Crea l'oggetto di autenticazione
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities); // Username e autorità dal token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Imposta l'autenticazione nel SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Autenticazione impostata nel SecurityContextHolder per l'utente: {}", username);
            } else {
                logger.warn("Il token JWT NON è valido");
            }
        }

        // Continua la catena di filtri
        filterChain.doFilter(request, response);
        logger.info("JwtRequestFilter completato");
    }
}