package se.systementor.utbildning.model;

import se.systementor.utbildning.exception.InvalidAttackException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Anger hur Jackson ska känna igen olika typer av attacker i JSON.
// Fältet "attackType" avgör vilken underklass som ska skapas.
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "attackType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = DamageAttack.class,
                name = "damage"
        )
})

// Abstrakt basklass för attacker.
// Samlar egenskaper och validering som alla attacker har gemensamt.
// Implementerar BattleAction så att attacker kan hanteras polymorft
public abstract class Attack implements BattleAction {
    // Fälten är privata för att skydda objektets data.
    // final innebär att värdena inte kan tilldelas på nytt efter skapandet.
    private final String name;
    private final Type type;
    private final int baseDamage;
    private final int accuracy;

    // Skapar en attack med namn, skada, träffsäkerhet och typ.
    // Jackson använder @JsonCreator och @JsonProperty för att
    // koppla värden från JSON till konstruktorns parametrar.
    @JsonCreator
    public Attack(
            @JsonProperty("name") String name,
            @JsonProperty("baseDamage") int baseDamage,
            @JsonProperty("accuracy") int accuracy,
            @JsonProperty("type") Type type
    ) {
        // Validerar alla värden innan de sparas i objektet.
        // Det förhindrar att en attack skapas med ogiltiga egenskaper.
        validateName(name);
        validateType(type);
        validateBaseDamage(baseDamage);
        validateAccuracy(accuracy);

        // Tilldelar fälten först när alla kontroller har godkänts.
        this.name = name;
        this.type = type;
        this.baseDamage = baseDamage;
        this.accuracy = accuracy;
    }

    // Getters ger andra klasser möjlighet att läsa egenskaperna
    // utan att få direkt åtkomst till de privata fälten.
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

    // Kontrollerar att attackens namn följer reglerna.
    private void validateName(String name) {
        // Namnet måste finnas och får inte bara innehålla mellanslag.
        if (name == null || name.isBlank()) {
            throw new InvalidAttackException("Attacknamnet får inte vara tomt.");
        }

        // Tillåter bara bokstäver och mellanslag i attacknamnet.
        if (!name.matches("[a-zA-ZåäöÅÄÖ ]+")) {
            throw new InvalidAttackException(
                    "Namnet får bara innehålla bokstäver och mellanslag."
            );
        }
    }

    // Kontrollerar att attacken har en giltig Pokemon-typ.
    private void validateType(Type type) {
        // En attack måste ha en typ för att typfördelar ska kunna beräknas.
        if (type == null) {
            throw new InvalidAttackException("Attacktypen får inte vara null.");
        }
    }

    // Kontrollerar att attackens grundskada ligger inom tillåtna gränser.
    private void validateBaseDamage(int baseDamage) {
        // Ser till att skadan är mellan 1 och 999.
        if (baseDamage < 1 || baseDamage > 999) {
            throw new InvalidAttackException(
                    "Skadan måste vara mellan 1 och 999."
            );
        }
    }

    // Kontrollerar att träffsäkerheten anges som ett procentvärde.
    private void validateAccuracy(int accuracy) {
        // Ser till att träffsäkerheten är mellan 0 och 100.
        if (accuracy < 0 || accuracy > 100) {
            throw new InvalidAttackException("Träffsäkerheten måste vara mellan 0 och 100.");
        }
    }

    // Uppfyller kontraktet från BattleAction med en mer specifik returtyp.
    // Metoden är abstrakt eftersom varje underklass själv ska
    // bestämma hur attacken utförs.
    @Override
    public abstract AttackResult execute(
            Pokemon attacker,
            Pokemon defender
    );

    // Implementerar BattleActions metod för att visa handlingens namn.
    // Alla attackklasser kan återanvända denna implementation.
    @Override
    public String label() {
        return getName();
    }
}
