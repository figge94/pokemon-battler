package se.systementor.utbildning.exception;

// Egen exception för fel som gäller attacker.
public class InvalidAttackException extends RuntimeException {
    public InvalidAttackException(String message) {
        super(message);
    }
}
