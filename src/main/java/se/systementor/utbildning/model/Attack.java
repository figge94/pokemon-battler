package se.systementor.utbildning.model;

import se.systementor.utbildning.exception.InvalidAttackException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Attack {
    // Gör fälten private så att de inte kan ändras direkt utifrån.
    private final String name;
    private final Type type;
    private final int baseDamage;
    private final int accuracy;

    // Validerar värdena direkt när en attack skapas.
    // Jackson använder den här konstruktorn när Attack läses in från JSON.
    @JsonCreator
    public Attack(
            @JsonProperty("name") String name,
            @JsonProperty("baseDamage") int baseDamage,
            @JsonProperty("accuracy") int accuracy,
            @JsonProperty("type") Type type
    ) {
        validateName(name);
        validateType(type);
        validateBaseDamage(baseDamage);
        validateAccuracy(accuracy);

        this.name = name;
        this.type = type;
        this.baseDamage = baseDamage;
        this.accuracy = accuracy;
    }

    public String getName() {
        return name;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public Type getType() {
        return type;
    }

    private void validateName(String name) {
        // Ser till att attacknamnet inte är tomt.
        if (name == null || name.isBlank()) {
            throw new InvalidAttackException("Attacknamnet får inte vara tomt.");
        }

        // Kollar att namnet bara innehåller bokstäver och mellanslag.
        if (!name.matches("[a-zA-ZåäöÅÄÖ ]+")) {
            throw new InvalidAttackException(
                    "Namnet får bara innehålla bokstäver och mellanslag."
            );
        }
    }

    private void validateType(Type type) {
        // En attack måste alltid ha en typ.
        if (type == null) {
            throw new InvalidAttackException("Attacktypen får inte vara null.");
        }
    }

    private void validateBaseDamage(int baseDamage) {
        // Ser till att skadan är mellan 0 och 999.
        if (baseDamage < 0 || baseDamage > 999) {
            throw new InvalidAttackException(
                    "Skadan måste vara mellan 0 och 999."
            );
        }
    }

    private void validateAccuracy(int accuracy) {
        // Ser till att träffsäkerheten är mellan 0 och 100.
        if (accuracy < 0 || accuracy > 100) {
            throw new InvalidAttackException("Träffsäkerheten måste vara mellan 0 och 100.");
        }

    }
}
