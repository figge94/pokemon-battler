package se.systementor.utbildning.model;

// Record används eftersom flyktresultatet inte behöver några egna fält.
// Implementerar ActionResult så att olika resultat kan hanteras
// genom samma interface (polymorfism).
public record FleeResult() implements ActionResult {
}
