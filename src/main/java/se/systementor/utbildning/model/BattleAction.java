package se.systementor.utbildning.model;

public interface BattleAction {

    String label();

    // Utför handlingen och returnerar dess resultat.
    ActionResult execute(Pokemon attacker, Pokemon defender);

    // Standardvärdet är att handlingen tar en hel tur.
    default boolean consumesTurn() {
        return true;
    }

    // Anger om handlingen avslutar striden.
    default boolean endsBattle() {
        return false;
    }
}
