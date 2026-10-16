package se.systementor.utbildning.model;

// Implementerar BattleAction så att flykt kan hanteras
// som andra stridshandlingar genom polymorfism.
public class FleeAction implements BattleAction {

    // Implementerar label från BattleAction.
    // Returnerar texten som ska visas i stridsmenyn.
    @Override
    public String label() {
        return "Fly";
    }

    // Implementerar execute från BattleAction.
    // Flykt orsakar ingen skada och använder därför inte parametrarna.
    @Override
    public FleeResult execute(Pokemon attacker, Pokemon defender) {

        // Skapar ett resultat som visar att flykthandlingen har utförts.
        // Stridssystemet ansvarar för att tolka resultatet
        // och uppdatera stridens tillstånd.
        return new FleeResult();
    }

    // Skriver över standardbeteendet i BattleAction så att flykt avslutar striden.
    @Override
    public boolean endsBattle() {
        return true;
    }
}
