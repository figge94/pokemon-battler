package se.systementor.utbildning.model;

// Lagrar resultatet av en attack.
// Record skapar automatiskt konstruktor och metoder för att läsa värdena.
// Implementerar ActionResult så olika resultat kan hanteras med polymorfism.
public record AttackResult(

        boolean hit,          // Anger om attacken träffade motståndaren.
        int damage,           // Hur mycket skada attacken orsakade.
        boolean criticalHit,  // Anger om attacken blev en kritisk träff.
        double effectiveness  // Anger hur effektiv attacken är mot motståndarens typ.

// Implementerar ActionResult så att olika typer av
// handlingsresultat kan hanteras genom samma interface.
) implements ActionResult {
}
