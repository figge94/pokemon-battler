package se.systementor.utbildning;

import se.systementor.utbildning.exception.InvalidAttackException;

public class Attack {
    // Gör fälten private så att de inte kan ändras direkt utifrån.
    private String name;
    private Type type;
    private int baseDamage;
    private int accuracy;

    // Behövs för att Jackson ska kunna läsa in från JSON.
    public Attack() {
    }

    // Använder setters så att värdena valideras direkt när en attack skapas.
    public Attack(String name, int baseDamage, int accuracy, Type type) {
        setName(name);
        setType(type);
        setBaseDamage(baseDamage);
        setAccuracy(accuracy);
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

    public void setName(String name) {
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

        this.name = name;
    }

    public void setType(Type type) {
        // En attack måste alltid ha en typ.
        if (type == null) {
            throw new InvalidAttackException("Attacktypen får inte vara null.");
        }

        this.type = type;
    }

    public void setBaseDamage(int baseDamage) {
        // Ser till att skadan är mellan 0 och 999.
        if (baseDamage < 0 || baseDamage > 999) {
            throw new InvalidAttackException(
                    "Skadan måste vara mellan 0 och 999."
            );
        }

        this.baseDamage = baseDamage;
    }

    public void setAccuracy(int accuracy) {
        // Ser till att träffsäkerheten är mellan 0 och 100.
        if (accuracy < 0 || accuracy > 100) {
            throw new InvalidAttackException("Träffsäkerheten måste vara mellan 0 och 100.");
        }

        this.accuracy = accuracy;
    }
}
