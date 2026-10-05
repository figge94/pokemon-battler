package se.systementor.utbildning.service;

import se.systementor.utbildning.ui.InputHelper;
import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

import java.util.List;
import java.util.Scanner;

public class AttackService {
    // Lägger till en ny attack på vald Pokemon.
    public static void addAttackToPokemon(Pokemon pokemon, Scanner scanner) {
        // Kollar direkt om Pokemon redan har max antal attacker,
        // så användaren inte behöver fylla i en ny attack i onödan.
        if (pokemon.getAttacks().size() >= 4) {
            System.out.println("En Pokemon kan ha max 4 attacker.");
            return;
        }

        // Läser in och validerar attacknamnet.
        String attackName = InputHelper.readValidName(scanner, "Attacknamn: ");

        // Läser in skada och ser till att värdet är mellan 0 och 999.
        int damage = InputHelper.readIntInRange(
                scanner,
                "Skada: ",
                0,
                999
        );

        // Läser in träffsäkerhet och ser till att värdet är mellan 0 och 100.
        int accuracy = InputHelper.readIntInRange(
                scanner,
                "Träffsäkerhet (0-100): ",
                0,
                100
        );

        // Låter användaren välja typ från Type-enum.
        Type attackType = InputHelper.chooseType(scanner);

        // Skapar attacken först när all input är giltig.
        Attack newAttack = new Attack(
                attackName,
                damage,
                accuracy,
                attackType
        );

        // Försöker lägga till attacken och fångar fel från Pokemon-klassen.
        try {
            pokemon.addAttack(newAttack);

            System.out.println(
                    pokemon.getName() + " har fått attacken " + newAttack.getName()
            );
        } catch (InvalidAttackException e) {
            System.out.println("Fel: " + e.getMessage());
        }

        System.out.println("\n=== FÖRHANDSVISNING ===");
        PokemonService.printDetails(pokemon);
    }

    // Tar bort en attack från vald Pokemon.
    public static void removeAttackFromPokemon(Pokemon pokemon, Scanner scanner) {
        // Hämtar en kopia av Pokemons attacker för att kunna visa dem.
        List<Attack> attacks = pokemon.getAttacks();

        // Stoppar borttagning om Pokemon bara har en attack kvar.
        if (attacks.size() <= 1) {
            System.out.println("En Pokemon måste ha minst 1 attack.");
            return;
        }

        System.out.println("Välj attack att ta bort:");

        // Visar alla attacker med ett nummer som användaren kan välja mellan.
        for (int i = 0; i < attacks.size(); i++) {
            System.out.println((i + 1) + ": " + attacks.get(i).getName());
        }

        // Läser in användarens val och ser till att det är mellan 1 och antal attacker.
        int attackChoice = InputHelper.readIntInRange(
                scanner,
                "Välj: ",
                1,
                attacks.size()
        );

        // Användaren väljer från 1, men listans första plats har index 0.
        Attack attackToRemove = attacks.get(attackChoice - 1);

        // Försöker ta bort attacken och fångar fel från Pokemon-klassen.
        try {
            pokemon.removeAttack(attackToRemove);
            System.out.println(attackToRemove.getName() + " har tagits bort.");
        } catch (InvalidAttackException e) {
            System.out.println("Det gick inte att ta bort attacken. " + e.getMessage());
        }

        System.out.println("\n=== FÖRHANDSVISNING ===");
        PokemonService.printDetails(pokemon);
    }

    // Skapar en standardattack beroende på vald Pokemon-typ.
    public static Attack createDefaultAttack(Type type) {
        return switch (type) {
            case FIRE -> new Attack("Flame Wheel", 60, 100, Type.FIRE);
            case WATER -> new Attack("Water Gun", 40, 100, Type.WATER);
            case GRASS -> new Attack("Razor Leaf", 55, 95, Type.GRASS);
            case ELECTRIC -> new Attack("Thunder Shock", 40, 100, Type.ELECTRIC);
            case NORMAL -> new Attack("Scratch", 40, 100, Type.NORMAL);
            case ICE -> new Attack("Ice Shard", 40, 100, Type.ICE);
            case PSYCHIC -> new Attack("Psychic", 90, 100, Type.PSYCHIC);
            case BUG -> new Attack("Skitter Smack", 70, 90, Type.BUG);
            case FAIRY -> new Attack("Fairy Wind", 40, 100, Type.FAIRY);
        };
    }
}
