package se.systementor.utbildning;

import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.exception.InvalidPokemonException;

import java.util.ArrayList;

public class Pokemon {
    // Gör fälten private så att de inte kan ändras direkt utifrån.
    private int id;
    private String name;
    private Type type;
    private int maxHp;
    private int currentHp;
    private ArrayList<Attack> attacks = new ArrayList<>();

    // Behövs för att Jackson ska kunna läsa in från JSON.
    public Pokemon() {
    }

    @Override
    public String toString() {
         StringBuilder pokemonDetails = new StringBuilder(
            String.format(
                    "%d. Namn: %s | TYPE: %s | HP: %d/%d%n",
                    getId(),
                    getName(),
                    getType(),
                    getCurrentHp(),
                    getMaxHp()
            )
        );

        // Räknar ut hur många tecken ID:t har för att kunna göra snygg indragning.
        int numberOfCharactersInId =
                String.valueOf(getId()).length();

        for (Attack attack : getAttacks()) {
            pokemonDetails.append(

             String.format (
                    "%s  Attack: %s | Skada: %d | Träffsäkerhet: %d | TYPE: %s%n",
                    " ".repeat(numberOfCharactersInId),
                    attack.getName(),
                    attack.getBaseDamage(),
                    attack.getAccuracy(),
                    attack.getType()
            )
            );
        }
        return pokemonDetails.toString();
    }

    // Använder setters så att värdena valideras direkt när en Pokemon skapas.
    public Pokemon(int id, String name, Type type, int maxHp, int currentHp) {
        setId(id);
        setName(name);
        setType(type);
        setMaxAndCurrentHp(maxHp);
        setCurrentHp(currentHp);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public int getMaxHp() {
        return this.maxHp;
    }

    public int getCurrentHp() {
        return this.currentHp;
    }

    // Skickar tillbaka en kopia så att original-listan inte kan ändras utifrån.
    public ArrayList<Attack> getAttacks() {
        return new ArrayList<>(attacks);
    }

    // Kollar att listan har rätt antal attacker innan den sparas.
    public void setAttacks(ArrayList<Attack> attacks) {
        if (attacks == null || attacks.isEmpty() || attacks.size() > 4) {
            throw new InvalidAttackException("En Pokemon måste ha mellan 1 och 4 attacker.");
        }

        if (attacks.contains(null)) {
            throw new InvalidAttackException("Attacklistan får inte innehålla null.");
        }

        // Sparar en kopia så att ändringar utanför Pokemon inte påverkar listan här.
        this.attacks = new ArrayList<>(attacks);
    }

    public void addAttack(Attack attack) {
        // Ser till att attacken inte är null.
        if (attack == null) {
            throw new InvalidAttackException("Attack får inte vara null.");
        }

        // En Pokemon får aldrig ha fler än 4 attacker.
        if (attacks.size() >= 4) {
            throw new InvalidAttackException("En Pokemon kan ha max 4 attacker.");
        }

        attacks.add(attack);
    }

    public void removeAttack(Attack attack) {
        // Ser till att attacken inte är null.
        if (attack == null) {
            throw new InvalidAttackException("Attack får inte vara null.");
        }

        // Hindrar att den sista attacken tas bort så att Pokemon alltid har minst 1 attack.
        if (attacks.size() <= 1) {
            throw new InvalidAttackException("En Pokemon måste ha minst 1 attack.");
        }

        // Kollar att attacken faktiskt finns innan den tas bort.
        if (!attacks.contains(attack)) {
            throw new InvalidAttackException(name + " har inte den attacken.");
        }

        attacks.remove(attack);
    }

    public void setId(int id) {
        // ID måste vara positivt för att vara giltigt.
        if (id <= 0) {
            throw new InvalidPokemonException("ID måste vara större än 0.");
        }

        this.id = id;
    }

    public void setName(String name) {
        // Ser till att namnet inte är tomt.
        if (name == null || name.isBlank()) {
            throw new InvalidPokemonException("Namnet får inte vara tomt.");
        }

        // Kollar att namnet bara innehåller tillåtna tecken.
        if (!name.matches("[a-zA-ZåäöÅÄÖ ]+")) {
            throw new InvalidPokemonException(
                    "Namnet får bara innehålla bokstäver och mellanslag."
            );
        }

        this.name = name;
    }

    public void setType(Type type) {
        // En Pokemon måste alltid ha en typ.
        if (type == null) {
            throw new InvalidPokemonException("Typ får inte vara null.");
        }

        this.type = type;
    }

    public void setCurrentHp(int currentHp) {
        // Ser till att currentHp är mellan 0 och maxHp.
        if (currentHp < 0 || currentHp > maxHp) {
            throw new InvalidPokemonException(
                    "Nuvarande HP måste vara mellan 0 och Max HP."
            );
        }

        this.currentHp = currentHp;
    }

    // Sätter currentHp till samma värde som maxHp.
    public void setMaxAndCurrentHp(int maxHp) {
        // Ser till att maxHp är mellan 1 och 999.
        if (maxHp <= 0) {
            throw new InvalidPokemonException("Max HP måste vara större än 0.");
        }

        if (maxHp > 999) {
            throw new InvalidPokemonException("Max HP får inte vara mer än 999.");
        }

        this.maxHp = maxHp;

        // När max HP ändras sätts currentHp till samma värde.
        setCurrentHp(maxHp);
    }
}
