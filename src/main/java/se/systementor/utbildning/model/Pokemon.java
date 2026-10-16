package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.exception.InvalidPokemonException;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
    private final int id;
    private final String name;
    private final Type type;
    private final int maxHp;

    // Dessa värden kan förändras under spelets gång.
    private int currentHp;
    private int speed;

    private final List<Attack> attacks = new ArrayList<>();

    // Pokemon med samma ID räknas som lika.
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Pokemon other)) {
            return false;
        }

        return id == other.id;
    }

    // Använder ID för att skapa en hashkod.
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    // Jackson använder konstruktorn vid JSON-inläsning.
    // JsonProperty kopplar JSON-fält till rätt parametrar.
    @JsonCreator
    public Pokemon(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("type") Type type,
            @JsonProperty("maxHp") int maxHp,
            @JsonProperty("speed") int speed
    ) {
        // Validerar alla värden innan objektets fält tilldelas.
        // Detta förhindrar att en Pokemon skapas med ogiltiga egenskaper.
        validateId(id);
        validateName(name);
        validateType(type);
        validateMaxHp(maxHp);
        validateSpeed(speed);

        // Sparar de godkända värdena i objektets privata fält.
        // En ny Pokemon börjar med full hälsa.
        this.id = id;
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.speed = speed;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getSpeed() {
        return speed;
    }

    // Returnerar en oföränderlig kopia så listan inte kan ändras utifrån.
    public List<Attack> getAttacks() {
        return List.copyOf(attacks);
    }

    // Lägger till en attack, max fyra tillåtna.
    public void addAttack(Attack attack) {

        if (attack == null) {
            throw new InvalidAttackException(
                    "Attack får inte vara null."
            );
        }

        if (attacks.size() >= 4) {
            throw new InvalidAttackException(
                    "En Pokemon kan ha max 4 attacker."
            );
        }

        attacks.add(attack);
    }

    // Ersätter hela attacklistan, exempelvis vid JSON-inläsning
    // eller när en ändring behöver återställas efter ett sparfel.
    @JsonProperty("attacks")
    public void replaceAttacks(List<Attack> newAttacks) {

        if (newAttacks == null || newAttacks.isEmpty() || newAttacks.size() > 4) {
            throw new InvalidAttackException(
                    "En Pokemon måste ha mellan 1 och 4 attacker."
            );
        }

        for (Attack attack : newAttacks) {
            if (attack == null) {
                throw new InvalidAttackException(
                        "Attacklistan får inte innehålla null."
                );
            }
        }

        // Ersätter den gamla listan först efter godkänd validering.
        attacks.clear();
        attacks.addAll(newAttacks);
    }

    // Tar bort en attack, men en Pokemon måste ha minst 1 attack för att det ska fungera.
    public void removeAttack(Attack attack) {

        if (attack == null) {
            throw new InvalidAttackException(
                    "Attack får inte vara null."
            );
        }

        // Förhindrar att sista attacken tas bort.
        if (attacks.size() <= 1) {
            throw new InvalidAttackException(
                    "En Pokemon måste ha minst 1 attack."
            );
        }

        if (!attacks.contains(attack)) {
            throw new InvalidAttackException(
                    name + " har inte den attacken."
            );
        }

        attacks.remove(attack);
    }

    // Minskar en Pokemons hälsa när den blivit träffad.
    public void takeDamage(int damage) {

        // Negativ skada är inte tillåten.
        if (damage < 0) {
            throw new IllegalArgumentException(
                    "Skada får inte vara negativ."
            );
        }

        // HP kan aldrig bli lägre än noll.
        currentHp = Math.max(0, currentHp - damage);
    }

    // Återställer en Pokemons hälsa.
    public void heal(int amount) {

        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Healing får inte vara negativ."
            );
        }

        // HP kan aldrig överstiga maxHp.
        currentHp += Math.min(amount, maxHp - currentHp);
    }

    // Återställer Pokemonens hälsa helt efter en strid.
    public void restoreHealth() {
        currentHp = maxHp;
    }

    // Skapar en kopia med nytt namn.
    public Pokemon withName(String newName) {
        return copyWith(newName, type, maxHp, speed);
    }

    // Skapar en kopia med ny typ.
    public Pokemon withType(Type newType) {
        return copyWith(name, newType, maxHp, speed);
    }

    // Skapar en kopia med nytt maxHp.
    public Pokemon withMaxHp(int newMaxHp) {
        return copyWith(name, type, newMaxHp, speed);
    }

    // Skapar en kopia med ny snabbhet.
    public Pokemon withSpeed(int newSpeed) {
        return copyWith(name, type, maxHp, newSpeed);
    }

    // Skapar en ny Pokemon med ändrade egenskaper men har fortfarande samma ID.
    private Pokemon copyWith(String name, Type type, int maxHp, int speed) {
        Pokemon updatedPokemon = new Pokemon(
                id,
                name,
                type,
                maxHp,
                speed
        );

        // Behåller HP utan att överskrida nya maxHp.
        updatedPokemon.currentHp = Math.min(currentHp, maxHp);

        // Kopierar attackerna till den nya Pokemon.
        updatedPokemon.attacks.addAll(attacks);

        return updatedPokemon;
    }

    // JsonIgnore förhindrar att värdet sparas i JSON.
    @JsonIgnore
    // Om currentHp är noll så har man blivit utslagen.
    public boolean isKnockedOut() {
        return currentHp == 0;
    }

    // Kontrollerar att ett ID är ett positivt heltal.
    private static void validateId(int id) {
        if (id <= 0) {
            throw new InvalidPokemonException(
                    "ID måste vara större än 0."
            );
        }
    }

    // Kontrollerar att Pokemons namn följer programmets regler.
    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidPokemonException(
                    "Namnet får inte vara tomt."
            );
        }

        if (!name.matches("[a-zA-ZåäöÅÄÖ ]+")) {
            throw new InvalidPokemonException(
                    "Namnet får bara innehålla bokstäver och mellanslag."
            );
        }
    }

    // Kontrollerar att en Pokemon har en giltig typ.
    private static void validateType(Type type) {
        if (type == null) {
            throw new InvalidPokemonException(
                    "Typ får inte vara null."
            );
        }
    }

    // Kontrollerar att maxHp ligger mellan 1 och 999.
    private static void validateMaxHp(int maxHp) {
        if (maxHp < 1 || maxHp > 999) {
            throw new InvalidPokemonException(
                    "Max HP måste vara mellan 1 och 999."
            );
        }
    }

    // Kontrollerar att Pokemons snabbhet ligger mellan 0 och 200.
    private static void validateSpeed(int speed) {
        if (speed < 0 || speed > 200) {
            throw new InvalidPokemonException(
                    "Snabbheten måste vara mellan 0 och 200."
            );
        }
    }
}
