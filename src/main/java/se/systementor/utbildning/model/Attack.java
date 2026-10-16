package se.systementor.utbildning.model;

import se.systementor.utbildning.exception.InvalidAttackException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Anger hur Jackson identifierar vilken underklass som ska skapas.
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, // Använder ett typnamn från JSON.
        property = "attackType" // JSON-fältet som innehåller typnamnet.
)

// Kopplar typnamnet i JSON till rätt underklass.
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = DamageAttack.class, // Klassen som ska skapas.
                name = "damage" // Typnamnet i JSON.
        )
})

// Abstrakt basklass med gemensamma egenskaper för attacker.
// Implementerar BattleAction för att hantera attacker med polymorfism.
// Används bara av DamageAttack, men kan utökas i framtiden med attacker som inte gör skada.
public abstract class Attack implements BattleAction {
    private final String name;
    private final Type type;
    private final int baseDamage;
    private final int accuracy;

    // Anger vilken konstruktor Jackson använder vid JSON-inläsning.
    @JsonCreator
    public Attack(
            // Kopplar JSON-fältet "name" till parametern name.
            // Övriga JsonProperty fungerar på samma sätt.
            @JsonProperty("name") String name,
            @JsonProperty("baseDamage") int baseDamage,
            @JsonProperty("accuracy") int accuracy,
            @JsonProperty("type") Type type
    ) {
        // Validerar värden innan attacken skapas.
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

    // Kontrollerar att en attack har ett giltigt namn.
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidAttackException("Attacknamnet får inte vara tomt.");
        }

        if (!name.matches("[a-zA-ZåäöÅÄÖ ]+")) {
            throw new InvalidAttackException(
                    "Namnet får bara innehålla bokstäver och mellanslag."
            );
        }
    }

    // Kontrollerar att en attack har en giltig Pokemon-typ.
    private void validateType(Type type) {
        if (type == null) {
            throw new InvalidAttackException("Attacktypen får inte vara null.");
        }
    }

    // Kontrollerar att en attacks grundskada ligger mellan 1 och 999.
    private void validateBaseDamage(int baseDamage) {
        if (baseDamage < 1 || baseDamage > 999) {
            throw new InvalidAttackException(
                    "Skadan måste vara mellan 1 och 999."
            );
        }
    }

    // Kontrollerar att träffsäkerheten är mellan 0 och 100.
    private void validateAccuracy(int accuracy) {
        if (accuracy < 0 || accuracy > 100) {
            throw new InvalidAttackException("Träffsäkerheten måste vara mellan 0 och 100.");
        }
    }

    // Varje underklass implementerar hur attacken utförs.
    @Override
    public abstract AttackResult execute(
            Pokemon attacker,
            Pokemon defender
    );

    // Returnerar attackens namn som handlingens namn.
    @Override
    public String label() {
        return getName();
    }
}
