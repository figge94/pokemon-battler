package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.exception.InvalidPokemonException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
    // Gör fälten private så att de inte kan ändras direkt utifrån.
    private final int id;
    private final String name;
    private final Type type;
    private final int maxHp;
    private int currentHp;
    private int speed;

    private final List<Attack> attacks = new ArrayList<>();

    // Två Pokemon med samma ID räknas som samma Pokemon.
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

    // Pokemon med samma ID ska ha samma hashkod.
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        StringBuilder pokemonDetails = new StringBuilder(
                String.format(
                        "%d. Namn: %s | Typ: %s | HP: %d/%d | Snabbhet: %d%n ",
                        getId(),
                        getName(),
                        getType().getLabel(),
                        getCurrentHp(),
                        getMaxHp(),
                        getSpeed()
                )
        );

        // Räknar ut hur många tecken ID:t har för att kunna göra snygg indragning.
        int numberOfCharactersInId =
                String.valueOf(getId()).length();

        for (Attack attack : getAttacks()) {
            pokemonDetails.append(

                    String.format(
                            "%s  Attack: %s | Skada: %d | Träffsäkerhet: %d | Typ: %s%n",
                            " ".repeat(numberOfCharactersInId),
                            attack.getName(),
                            attack.getBaseDamage(),
                            attack.getAccuracy(),
                            attack.getType().getLabel()
                    )
            );
        }
        return pokemonDetails.toString();
    }

    // Jackson använder den här konstruktorn när Pokemon läses in från JSON.
    // @JsonCreator visar vilken konstruktor som ska användas.
    // @JsonProperty kopplar värden från JSON till rätt parameter.
    @JsonCreator
    public Pokemon(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("type") Type type,
            @JsonProperty("maxHp") int maxHp,
            @JsonProperty("speed") int speed
    ) {
        validateId(id);
        validateName(name);
        validateType(type);
        validateMaxHp(maxHp);
        validateSpeed(speed);

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

    // Skickar tillbaka en kopia så att original-listan inte kan ändras utifrån.
    public List<Attack> getAttacks() {
        return List.copyOf(attacks);
    }


    public void addAttack(Attack attack) {
        // Ser till att attacken inte är null.
        if (attack == null) {
            throw new InvalidAttackException(
                    "Attack får inte vara null."
            );
        }

        // En Pokemon får aldrig ha fler än 4 attacker.
        if (attacks.size() >= 4) {
            throw new InvalidAttackException(
                    "En Pokemon kan ha max 4 attacker."
            );
        }

        attacks.add(attack);
    }

    // Läser in och validerar Pokemonens attacker från JSON.
    @JsonProperty("attacks")
    public void changeAttacks(List<Attack> newAttacks) {
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

        attacks.clear();
        attacks.addAll(newAttacks);
    }

    public void removeAttack(Attack attack) {
        // Ser till att attacken inte är null.
        if (attack == null) {
            throw new InvalidAttackException(
                    "Attack får inte vara null."
            );
        }

        // Hindrar att den sista attacken tas bort så att Pokemon alltid har minst 1 attack.
        if (attacks.size() <= 1) {
            throw new InvalidAttackException(
                    "En Pokemon måste ha minst 1 attack."
            );
        }

        // Kollar att attacken faktiskt finns innan den tas bort.
        if (!attacks.contains(attack)) {
            throw new InvalidAttackException(
                    name + " har inte den attacken."
            );
        }

        attacks.remove(attack);
    }

    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException(
                    "Skada får inte vara negativ."
            );
        }

        currentHp = Math.max(0, currentHp - damage);
    }

    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Healing får inte vara negativ."
            );
        }

        currentHp += Math.min(amount, maxHp - currentHp);
    }

    public void restoreHealth() {
        currentHp = maxHp;
    }

    // Skapar en ny Pokemon med ett nytt namn.
    public Pokemon withName(String newName) {
        return copyWith(newName, type, maxHp, speed);
    }

    // Skapar en ny Pokemon med en ny typ.
    public Pokemon withType(Type newType) {
        return copyWith(name, newType, maxHp, speed);
    }

    // Skapar en ny Pokemon med nytt max HP.
    public Pokemon withMaxHp(int newMaxHp) {
        return copyWith(name, type, newMaxHp, speed);
    }

    public Pokemon withSpeed(int newSpeed) {
        return copyWith(name, type, maxHp, newSpeed);
    }

    // Skapar en uppdaterad Pokemon och behåller HP och attacker.
    private Pokemon copyWith(String name, Type type, int maxHp, int speed) {
        Pokemon updatedPokemon = new Pokemon(
                id,
                name,
                type,
                maxHp,
                speed
        );

        // Behåller HP, men begränsar det till nya maxvärdet.
        updatedPokemon.currentHp = Math.min(currentHp, maxHp);

        // Kopierar attackerna till den nya Pokemonen.
        updatedPokemon.attacks.addAll(attacks);

        return updatedPokemon;
    }

    // Ignorerar att isKnockedOut sparas till JSON-filen
    @JsonIgnore
    public boolean isKnockedOut() {
        return currentHp == 0;
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new InvalidPokemonException(
                    "ID måste vara större än 0."
            );
        }
    }

    private void validateName(String name) {
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

    private void validateType(Type type) {
        if (type == null) {
            throw new InvalidPokemonException(
                    "Typ får inte vara null."
            );
        }
    }

    private void validateMaxHp(int maxHp) {
        if (maxHp < 1 || maxHp > 999) {
            throw new InvalidPokemonException(
                    "Max HP måste vara mellan 1 och 999."
            );
        }
    }

    private void validateSpeed(int speed) {
        // Ser till att träffsäkerheten är mellan 0 och 100.
        if (speed < 0 || speed > 200) {
            throw new InvalidPokemonException("Snabbheten måste vara mellan 0 och 200.");
        }
    }
}
