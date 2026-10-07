package se.systementor.utbildning.service;


import se.systementor.utbildning.ui.InputHelper;

import se.systementor.utbildning.repository.PokemonFileService;
import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.exception.InvalidPokemonException;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PokemonService {

    // Letar efter en Pokemon med samma namn, oavsett stora eller små bokstäver.
    public static Pokemon findPokemonByName(List<Pokemon> pokemons, String name) {
        for (Pokemon pokemon : pokemons) {
            if (pokemon.getName().equalsIgnoreCase(name)) {
                return pokemon;
            }
        }

        return null;
    }

    // Private eftersom metoden bara används i den här klassen.
    private static int getNextId(List<Pokemon> pokemons) {
        int maxId = 0;

        // Går igenom alla Pokemon för att hitta det högsta ID:t.
        for (Pokemon pokemon : pokemons) {
            if (pokemon.getId() > maxId) {
                maxId = pokemon.getId();
            }
        }

        // Returnerar nästa lediga ID genom att lägga till 1 på det högsta.
        return maxId + 1;
    }

    // Kontrollerar om ett Pokemon-namn redan finns i listan.
    private static boolean existsByName(List<Pokemon> pokemons, String name) {
        for (Pokemon pokemon : pokemons) {
            if (pokemon.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    // Kontrollerar om namnet används av någon annan Pokemon än den som redigeras.
    private static boolean existsByNameExceptId(
            List<Pokemon> pokemons,
            String name,
            int id
    ) {
        for (Pokemon pokemon : pokemons) {
            // Ignorerar den Pokemon som redigeras och kollar om någon annan har samma namn.
            if (pokemon.getId() != id &&
                    pokemon.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    // Visar alla Pokemon och deras attacker.
    public static void showAllPokemons(List<Pokemon> pokemons) {
        for (Pokemon pokemon : pokemons) {
            printDetails(pokemon);
            System.out.println();
        }
    }

    // Visar detaljer om vald Pokemon.
    public static void printDetails(Pokemon pokemon) {
        System.out.println(pokemon);
    }

    // Söker efter en Pokemon med hjälp av namn.
    public static void searchPokemon(List<Pokemon> pokemons, Scanner scanner) {
        System.out.println("Skriv namnet på en Pokemon:");
        String name = scanner.nextLine();

        Pokemon pokemon = findPokemonByName(pokemons, name);

        if (pokemon == null) {
            System.out.println("Pokemon hittades inte.");
            return;
        }

        printDetails(pokemon);
    }

    // Lägger till en ny Pokemon med validerad input.
    public static void addPokemon(List<Pokemon> pokemons, Scanner scanner) {
        // Hämtar nästa lediga ID så att varje Pokemon får ett eget ID.
        int id = getNextId(pokemons);

        System.out.println("Nästa Pokemon kommer få ID: " + id);

        String name = InputHelper.readValidName(scanner, "Namn: ");

        // Stoppar om namnet redan används av en annan Pokemon.
        if (existsByName(pokemons, name)) {
            System.out.println(name + " finns redan.");
            return;
        }

        // Läser in HP och ser till att värdet är mellan 1 och 999.
        int hp = InputHelper.readIntInRange(scanner, "HP: ", 1, 999);

        // Låter användaren välja Pokemon-typ.
        Type type = InputHelper.chooseType(scanner);

        try {
            Pokemon pokemon = new Pokemon(
                    id,
                    name,
                    type,
                    hp
            );

            // Skapar en lista för Pokemon-attacker.
            List<Attack> attacks = new ArrayList<>();

            // Skapar en standardattack beroende på Pokemon-typ.
            Attack attack = AttackService.createDefaultAttack(type);
            attacks.add(attack);

            pokemon.changeAttacks(attacks);
            System.out.println("\n=== FÖRHANDSVISNING ===");
            printDetails(pokemon);

            boolean confirm = InputHelper.readYesNo(
                    scanner,
                    "\nBekräfta genom att skriva ja eller nej: "
            );

            // Lägger bara till Pokemon om användaren bekräftar.
            if (confirm) {
                pokemons.add(pokemon);

                PokemonFileService.savePokemonsToFile(pokemons);

                System.out.println("Pokemon tillagd och sparad.");
            } else {
                System.out.println("Avbrutet.");
            }
        } catch (InvalidAttackException e) {
            System.out.println("Det gick inte att lägga till attacken. " + e.getMessage());
        } catch (InvalidPokemonException e) {
            System.out.println("Det gick inte att lägga till pokemon. " + e.getMessage());
        }
    }

    // Ändrar namnet på en Pokemon.
    public static void editPokemonName(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            String newName
    ) {
        // Kontrollerar om namnet redan används av en annan Pokemon.
        if (existsByNameExceptId(pokemons, newName, pokemon.getId())) {
            System.out.println(newName + " finns redan.");
            return;
        }

        Pokemon updatedPokemon = new Pokemon(
                pokemon.getId(),
                newName,
                pokemon.getType(),
                pokemon.getMaxHp()
        );

        updatedPokemon.changeAttacks(
                new ArrayList<>(pokemon.getAttacks())
        );

        int index = pokemons.indexOf(pokemon);
        pokemons.set(index, updatedPokemon);

        System.out.println("\n=== FÖRHANDSVISNING ===");
        printDetails(updatedPokemon);
    }

    // Ändrar typ på en Pokemon.
    public static Pokemon editPokemonType(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Type newType
    ) {

        Pokemon updatedPokemon = new Pokemon(
                pokemon.getId(),
                pokemon.getName(),
                newType,
                pokemon.getMaxHp()
        );

        updatedPokemon.changeAttacks(
                new ArrayList<>(pokemon.getAttacks())
        );

        int index = pokemons.indexOf(pokemon);
        pokemons.set(index, updatedPokemon);

        return updatedPokemon;
    }

    // Ändrar currentHp på en Pokemon.
    public static void editPokemonCurrentHp(
            Pokemon pokemon,
            int hp
    ) {

        int difference = hp - pokemon.getCurrentHp();

        if (difference > 0) {
            pokemon.heal(difference);
        } else if (difference < 0) {
            pokemon.takeDamage(-difference);
        }

    }

    // Ändrar maxHp på en Pokemon.
    public static Pokemon editPokemonMaxHp(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            int hp
    ) {
        Pokemon updatedPokemon = new Pokemon(
                pokemon.getId(),
                pokemon.getName(),
                pokemon.getType(),
                hp
        );

        updatedPokemon.changeAttacks(
                new ArrayList<>(pokemon.getAttacks())
        );

        int index = pokemons.indexOf(pokemon);
        pokemons.set(index, updatedPokemon);

        return updatedPokemon;
    }

    // Tar bort en Pokemon efter bekräftelse.
    public static void removePokemon(List<Pokemon> pokemons, Scanner scanner) {

        System.out.println("Ange namn på den Pokemon du vill ta bort:");
        String name = scanner.nextLine();

        // Hämtar den Pokemon som användaren vill ta bort.
        Pokemon pokemon = findPokemonByName(pokemons, name);

        if (pokemon == null) {
            System.out.println("Pokemon hittades inte.");
            return;
        }

        System.out.println("Du är på väg att ta bort:");
        System.out.println(pokemon.getId() + ". " + pokemon.getName());

        boolean confirm = InputHelper.readYesNo(
                scanner,
                "Bekräfta genom att skriva ja eller nej: "
        );

        // Tar bara bort Pokemon om användaren bekräftar.
        if (confirm) {
            pokemons.remove(pokemon);

            System.out.println(pokemon.getName() + " har tagits bort.");
        } else {
            System.out.println("Borttagningen avbröts.");
        }
    }
}