package se.systementor.utbildning.exception;

// Egen exception för fel som uppstår när Pokemon-data sparas till fil.
public class PokemonSaveException extends RuntimeException {

    public PokemonSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}