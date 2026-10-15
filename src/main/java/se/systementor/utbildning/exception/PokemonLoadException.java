package se.systementor.utbildning.exception;

// Egen exception för fel som uppstår när Pokemon-data laddas från fil.
public class PokemonLoadException extends RuntimeException {

  // Skapar ett undantag med ett felmeddelande.
  public PokemonLoadException(String message) {
    super(message);
  }

  // Skapar ett undantag med både felmeddelande och ursprungligt fel.
  // Det gör det möjligt att behålla information om varför laddningen misslyckades.
  public PokemonLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}
