package se.systementor.utbildning.model;

// Gemensamt interface för alla handlingar under en strid.
// Gör att exempelvis attacker och flykt kan hanteras
// genom samma typ med hjälp av polymorfism.
public interface BattleAction {

    // Returnerar handlingens namn som visas i stridsmenyn.
    // Varje handling bestämmer själv vilken text den visar.
    String label();

    // Utför handlingen mot en annan Pokemon och returnerar resultatet.
    // Varje implementation ansvarar själv för hur handlingen utförs.
    ActionResult execute(Pokemon attacker, Pokemon defender);

    // Anger om handlingen förbrukar en tur.
    // Standardvärdet är true, men en implementation kan ändra detta.
    default boolean consumesTurn() {
        return true;
    }

    // Anger om handlingen kan avsluta striden.
    // Standardvärdet är false, men exempelvis FleeAction kan ändra detta.
    default boolean endsBattle() {
        return false;
    }
}
