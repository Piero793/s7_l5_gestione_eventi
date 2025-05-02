package it.epicode.s7_l5_gestione_eventi.security;


public class Exceptions extends RuntimeException {
    public Exceptions(String message) {
        super(message);
    }

    public static class CredenzialiNonValideException extends Exceptions {
        public CredenzialiNonValideException(String message) {
            super(message);
        }
    }

    public static class UtenteGiaEsistenteException extends Exceptions {
        public UtenteGiaEsistenteException(String message) {
            super(message);
        }
    }

    public static class AutorizzazioneNegataException extends Exceptions {
        public AutorizzazioneNegataException(String message) {
            super(message);
        }
    }
}
