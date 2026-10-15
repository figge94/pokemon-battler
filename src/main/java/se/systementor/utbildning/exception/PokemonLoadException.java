package se.systementor.utbildning.exception;

// Egen exception för fel när Pokemon-data laddas från fil.
public class PokemonLoadException extends RuntimeException {

  public PokemonLoadException(String message) {
    super(message);
  }

  public PokemonLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}
