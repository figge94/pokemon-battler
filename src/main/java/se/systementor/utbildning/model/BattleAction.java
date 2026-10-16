package se.systementor.utbildning.model;

// Gemensamt interface för stridshandlingar.
// Gör att attacker och flykt kan hanteras med polymorfism.
public interface BattleAction {

    // Returnerar handlingens namn som visas i menyn.
    String label();

    // Utför handlingen mot en annan Pokemon och returnerar resultatet.
    // Varje implementation ansvarar själv för hur handlingen utförs.
    ActionResult execute(Pokemon attacker, Pokemon defender);

    // Standardmetod som anger om handlingen avslutar striden.
    // FleeAction skriver över metoden och returnerar true.
    default boolean endsBattle() {
        return false;
    }
}
