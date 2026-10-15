
package se.systementor.utbildning.model;

public interface BattleAction {

    String label();

    void execute(Pokemon attacker, Pokemon defender);

    default boolean consumesTurn() {
        return true;
    }
}
