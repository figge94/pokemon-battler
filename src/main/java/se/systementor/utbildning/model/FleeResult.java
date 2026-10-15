package se.systementor.utbildning.model;

// Representerar resultatet av en flykthandling under striden.
// Använder record eftersom resultatet inte behöver några egna fält.
// Implementerar ActionResult så att flyktresultat och attackresultat
// kan hanteras genom samma interface med hjälp av polymorfism.
public record FleeResult() implements ActionResult {
}
