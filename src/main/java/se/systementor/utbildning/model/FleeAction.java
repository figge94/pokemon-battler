package se.systementor.utbildning.model;

// Representerar handlingen att fly från en strid.
// Implementerar BattleAction så att flykt kan hanteras
// på samma sätt som andra stridshandlingar genom polymorfism.
public class FleeAction implements BattleAction {

    // Implementerar label från BattleAction.
    // Returnerar texten som ska visas i stridsmenyn.
    @Override
    public String label() {
        return "Fly";
    }

    // Implementerar execute från BattleAction.
    // Flykt orsakar ingen skada och behöver därför
    // inte använda attacker eller försvararens egenskaper.
    @Override
    public FleeResult execute(Pokemon attacker, Pokemon defender) {

        // Skapar ett resultat som visar att flykthandlingen har utförts.
        // Stridssystemet ansvarar för att tolka resultatet
        // och uppdatera stridens tillstånd.
        return new FleeResult();
    }

    // Skriver över standardbeteendet från BattleAction.
    // En flykthandling ska avsluta striden.
    @Override
    public boolean endsBattle() {
        return true;
    }
}
