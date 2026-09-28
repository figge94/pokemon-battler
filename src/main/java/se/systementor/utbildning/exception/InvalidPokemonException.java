package se.systementor.utbildning.exception;

// Egen exception för fel som gäller Pokemon.
public class InvalidPokemonException extends RuntimeException {
    public InvalidPokemonException(String message) {
        super(message);
    }
}
