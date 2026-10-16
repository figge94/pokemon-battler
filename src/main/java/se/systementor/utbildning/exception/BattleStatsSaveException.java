package se.systementor.utbildning.exception;

// Används när stridsstatistiken inte kan sparas.
public class BattleStatsSaveException extends RuntimeException {

    public BattleStatsSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
