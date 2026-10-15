
package se.systementor.utbildning.model;

public class FleeAction implements BattleAction {

    // Namnet som visas i stridsmenyn.
    @Override
    public String label() {
        return "Fly";
    }

    // Försöker fly utan att göra skada.
    @Override
    public void execute(Pokemon attacker, Pokemon defender) {
        System.out.println(
                attacker.getName() + " försöker fly!"
        );
    }
}
