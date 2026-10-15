package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.exception.InvalidPokemonException;

import java.util.ArrayList;
import java.util.List;

// Representerar en Pokemon med egenskaper, hälsa och attacker.
// Klassen ansvarar själv för att dess värden följer spelets regler.
// Privata fält och publika metoder används för inkapsling.
public class Pokemon {

    // Grundläggande egenskaper för en Pokemon.
    // final innebär att dessa värden inte kan tilldelas på nytt
    // efter att objektet har skapats.
    private final int id;
    private final String name;
    private final Type type;
    private final int maxHp;

    // Dessa värden kan förändras under spelets gång.
    private int currentHp;
    private int speed;

    // Lagrar Pokemonens attacker i en privat lista.
    // Listan kan ändras genom klassens egna metoder.
    private final List<Attack> attacks = new ArrayList<>();

    // Jämför två Pokemon utifrån deras unika ID.
    // Två objekt med samma ID betraktas som samma Pokemon
    // även om de är olika objekt i minnet.
    @Override
    public boolean equals(Object obj) {

        // Om båda referenserna pekar på samma objekt är de lika.
        if (this == obj) {
            return true;
        }

        // Kontrollerar att objektet är en Pokemon.
        // instanceof skapar samtidigt variabeln other.
        if (!(obj instanceof Pokemon other)) {
            return false;
        }

        // Pokemon med samma ID räknas som lika.
        return id == other.id;
    }

    // Skapar en hashkod utifrån Pokemonens ID.
    // Objekt som är lika enligt equals måste ge samma hashkod.
    // Detta är viktigt om Pokemon används i HashSet eller HashMap.
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    // Skapar en Pokemon med dess grundläggande egenskaper.
    // Jackson använder konstruktorn vid inläsning från JSON.
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

    // Getters gör att andra klasser kan läsa egenskaperna
    // utan att få direkt åtkomst till de privata fälten.
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

    // Returnerar en skrivskyddad kopia av attacklistan.
    // Detta förhindrar att andra klasser ändrar listan direkt
    // och kringgår reglerna för hur många attacker som är tillåtna.
    public List<Attack> getAttacks() {
        return List.copyOf(attacks);
    }

    // Lägger till en attack om den följer Pokemonens regler.
    public void addAttack(Attack attack) {

        // Förhindrar att en null-referens läggs till i listan.
        if (attack == null) {
            throw new InvalidAttackException(
                    "Attack får inte vara null."
            );
        }

        // En Pokemon får ha högst fyra attacker.
        if (attacks.size() >= 4) {
            throw new InvalidAttackException(
                    "En Pokemon kan ha max 4 attacker."
            );
        }

        // Lägger till attacken först när kontrollerna är godkända.
        attacks.add(attack);
    }

    // Ersätter hela attacklistan, exempelvis vid JSON-inläsning
    // eller när en ändring behöver återställas efter ett sparfel.
    @JsonProperty("attacks")
    public void replaceAttacks(List<Attack> newAttacks) {

        // Kontrollerar att listan finns och innehåller 1–4 attacker.
        // Därmed kan ingen ogiltig attacklista sparas i objektet.
        if (newAttacks == null || newAttacks.isEmpty() || newAttacks.size() > 4) {
            throw new InvalidAttackException(
                    "En Pokemon måste ha mellan 1 och 4 attacker."
            );
        }

        // Går igenom listan för att säkerställa att ingen attack är null.
        for (Attack attack : newAttacks) {
            if (attack == null) {
                throw new InvalidAttackException(
                        "Attacklistan får inte innehålla null."
                );
            }
        }

        // Ersätter den gamla listan först efter godkänd validering.
        // Detta skyddar objektet från att hamna i ett ogiltigt tillstånd.
        attacks.clear();
        attacks.addAll(newAttacks);
    }

    // Tar bort en attack om Pokemon fortfarande har minst en kvar.
    public void removeAttack(Attack attack) {

        // Kontrollerar att en giltig attack har skickats in.
        if (attack == null) {
            throw new InvalidAttackException(
                    "Attack får inte vara null."
            );
        }

        // En Pokemon måste alltid ha minst en attack.
        // Därför får den sista attacken inte tas bort.
        if (attacks.size() <= 1) {
            throw new InvalidAttackException(
                    "En Pokemon måste ha minst 1 attack."
            );
        }

        // Kontrollerar att attacken tillhör denna Pokemon.
        if (!attacks.contains(attack)) {
            throw new InvalidAttackException(
                    name + " har inte den attacken."
            );
        }

        // Tar bort attacken efter att samtliga kontroller har godkänts.
        attacks.remove(attack);
    }

    // Minskar Pokemonens hälsa när den tar skada.
    public void takeDamage(int damage) {

        // Negativ skada är inte tillåten.
        if (damage < 0) {
            throw new IllegalArgumentException(
                    "Skada får inte vara negativ."
            );
        }

        // Math.max ser till att HP aldrig blir lägre än noll.
        currentHp = Math.max(0, currentHp - damage);
    }

    // Återställer en del av Pokemonens hälsa.
    public void heal(int amount) {

        // Förhindrar att ett negativt värde används för läkning.
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Healing får inte vara negativ."
            );
        }

        // Math.min begränsar läkningen så att HP inte överstiger maxHp.
        currentHp += Math.min(amount, maxHp - currentHp);
    }

    // Återställer Pokemonens hälsa helt efter en strid.
    public void restoreHealth() {
        currentHp = maxHp;
    }

    // Skapar en uppdaterad Pokemon med ett nytt namn.
    // Övriga egenskaper behålls genom copyWith.
    public Pokemon withName(String newName) {
        return copyWith(newName, type, maxHp, speed);
    }

    // Skapar en uppdaterad Pokemon med en annan typ.
    public Pokemon withType(Type newType) {
        return copyWith(name, newType, maxHp, speed);
    }

    // Skapar en uppdaterad Pokemon med ett nytt maximalt HP.
    public Pokemon withMaxHp(int newMaxHp) {
        return copyWith(name, type, newMaxHp, speed);
    }

    // Skapar en uppdaterad Pokemon med en annan snabbhet.
    public Pokemon withSpeed(int newSpeed) {
        return copyWith(name, type, maxHp, newSpeed);
    }

    // Gemensam hjälpmetod som skapar en kopia med ändrade egenskaper.
    // Används för att slippa duplicera samma kod i alla with-metoder.
    private Pokemon copyWith(String name, Type type, int maxHp, int speed) {

        // Skapar en ny Pokemon med samma ID och önskade egenskaper.
        // Konstruktorn utför valideringen även för det nya objektet.
        Pokemon updatedPokemon = new Pokemon(
                id,
                name,
                type,
                maxHp,
                speed
        );

        // Behåller nuvarande HP men begränsar det till det nya maxvärdet.
        updatedPokemon.currentHp = Math.min(currentHp, maxHp);

        // Kopierar attackerna så att den uppdaterade Pokemonen
        // får samma attacker utan att dela själva listan.
        updatedPokemon.attacks.addAll(attacks);

        // Returnerar det nya objektet till den anropande klassen.
        return updatedPokemon;
    }

    // Kontrollerar om Pokemon har blivit besegrad.
    // JsonIgnore förhindrar att detta beräknade värde sparas i JSON.
    @JsonIgnore
    public boolean isKnockedOut() {
        return currentHp == 0;
    }

    // Kontrollerar att Pokemonens ID är ett positivt heltal.
    private static void validateId(int id) {
        if (id <= 0) {
            throw new InvalidPokemonException(
                    "ID måste vara större än 0."
            );
        }
    }

    // Kontrollerar att Pokemonens namn följer programmets regler.
    private static void validateName(String name) {

        // Namnet får inte saknas eller bara bestå av mellanslag.
        if (name == null || name.isBlank()) {
            throw new InvalidPokemonException(
                    "Namnet får inte vara tomt."
            );
        }

        // Namnet får endast innehålla bokstäver och mellanslag.
        if (!name.matches("[a-zA-ZåäöÅÄÖ ]+")) {
            throw new InvalidPokemonException(
                    "Namnet får bara innehålla bokstäver och mellanslag."
            );
        }
    }

    // Kontrollerar att Pokemonen har en giltig typ.
    private static void validateType(Type type) {
        if (type == null) {
            throw new InvalidPokemonException(
                    "Typ får inte vara null."
            );
        }
    }

    // Kontrollerar att maximalt HP ligger inom tillåtet intervall.
    private static void validateMaxHp(int maxHp) {
        if (maxHp < 1 || maxHp > 999) {
            throw new InvalidPokemonException(
                    "Max HP måste vara mellan 1 och 999."
            );
        }
    }

    // Kontrollerar att Pokemons snabbhet ligger inom tillåtna gränser.
    private static void validateSpeed(int speed) {
        if (speed < 0 || speed > 200) {
            throw new InvalidPokemonException(
                    "Snabbheten måste vara mellan 0 och 200."
            );
        }
    }
}
