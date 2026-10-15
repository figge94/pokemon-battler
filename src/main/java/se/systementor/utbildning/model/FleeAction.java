
package se.systementor.utbildning.model;

public class FleeAction implements BattleAction {

    // Namnet som visas i stridsmenyn.
    @Override
    public String label() {
        return "Fly";
    }

    // Försöker fly utan att göra skada.
    @Override
    public FleeResult execute(Pokemon attacker, Pokemon defender) {
        // Returnerar ett flyktresultat utan att skriva till konsolen.
        return new FleeResult();
    }

    // Flykt ska avsluta striden.
    @Override
    public boolean endsBattle() {
        return true;
    }
}
