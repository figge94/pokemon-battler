package se.systementor.utbildning.model;

// Representerar resultatet efter att en attack har utförts.
// Använder record eftersom klassen bara behöver lagra resultatdata.
// Java skapar automatiskt konstruktor och metoder för att läsa värdena.
public record AttackResult(

        // Anger om attacken träffade motståndaren.
        boolean hit,

        // Hur mycket skada attacken orsakade.
        int damage,

        // Anger om attacken blev en kritisk träff.
        boolean criticalHit,

        // Anger attackens typfördel mot motståndaren.
        double effectiveness

// Implementerar ActionResult så att olika typer av
// handlingsresultat kan hanteras genom samma interface.
) implements ActionResult {
}
