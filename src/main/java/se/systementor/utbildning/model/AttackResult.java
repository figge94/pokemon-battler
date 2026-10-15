package se.systementor.utbildning.model;

// Innehåller resultatet av en utförd attack.
public record AttackResult(
        boolean hit,
        int damage,
        boolean criticalHit,
        double effectiveness
) implements ActionResult {
}