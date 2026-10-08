package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.InvalidPokemonException;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;
import se.systementor.utbildning.repository.PokemonFileService;
import se.systementor.utbildning.service.PokemonService;

import java.util.List;
import java.util.Scanner;

public class PokemonMenu {

    public static void addPokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        String name = InputHelper.readValidName(
                scanner,
                "Namn: "
        );

        int hp = InputHelper.readIntInRange(
                scanner,
                "HP: ",
                1,
                999
        );

        Type type = InputHelper.chooseType(scanner);

        Pokemon pokemon = PokemonService.createPokemon(
                pokemons,
                name,
                hp,
                type
        );

        System.out.println("\n=== FÖRHANDSVISNING ===");
        printDetails(pokemon);

        boolean confirm = InputHelper.readYesNo(
                scanner,
                "\nBekräfta genom att skriva ja eller nej: "
        );

        if (confirm) {
            PokemonService.addPokemon(pokemons, pokemon);

            PokemonFileService.savePokemonsToFile(pokemons);

            System.out.println("Pokemon tillagd och sparad.");
        } else {
            System.out.println("Avbrutet.");
        }
    }

    // Visar redigeringsmenyn och skickar vidare till rätt metod.
    public static void editPokemon(List<Pokemon> pokemons, Scanner scanner) {
        System.out.println("Ange namn på den Pokemon du vill redigera:");
        String name = scanner.nextLine();

        // Hämtar vald Pokemon innan redigeringsmenyn visas.
        Pokemon pokemon = PokemonService.findPokemonByName(pokemons, name);

        if (pokemon == null) {
            System.out.println(name + " hittades inte.");
            return;
        }

        System.out.println("\n=== FÖRHANDSVISNING ===");
        printDetails(pokemon);
        System.out.println();

        Menu.showEditMenu();

        int choice = InputHelper.readIntInRange(scanner, "Välj: ", 1, 7);

        // Skickar användaren vidare till rätt redigeringsmetod beroende på valet.
        switch (choice) {
            case 1 -> editPokemonName(pokemons, pokemon, scanner);
            case 2 -> editPokemonType(pokemons, pokemon, scanner);
            case 3 -> editPokemonCurrentHp(pokemon, scanner);
            case 4 -> editPokemonMaxHp(pokemons, pokemon, scanner);
            case 5 -> AttackMenu.handleAddAttack(pokemon, scanner);
            case 6 -> AttackMenu.removeAttackFromPokemon(pokemon, scanner);
            case 7 -> System.out.println("Går tillbaka till huvudmenyn.");
        }
    }

    // Ändrar namnet på en Pokemon.
    public static void editPokemonName(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        String newName = InputHelper.readValidName(
                scanner,
                "Nytt namn: "
        );

        try {
            Pokemon updatedPokemon = PokemonService.editPokemonName(
                    pokemons,
                    pokemon,
                    newName
            );

            System.out.println("\n=== FÖRHANDSVISNING ===");
            printDetails(updatedPokemon);

        } catch (InvalidPokemonException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void editPokemonType(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        Type newType = InputHelper.chooseType(scanner);

        Pokemon updatedPokemon = PokemonService.editPokemonType(
                pokemons,
                pokemon,
                newType
        );

        System.out.println("Typen är uppdaterad.");
        printDetails(updatedPokemon);
    }

    public static void editPokemonCurrentHp(
            Pokemon pokemon,
            Scanner scanner
    ) {
        int hp = InputHelper.readIntInRange(
                scanner,
                "Nytt nuvarande HP: ",
                0,
                pokemon.getMaxHp()
        );

        PokemonService.editPokemonCurrentHp(
                pokemon,
                hp
        );

        System.out.println("Nuvarande HP är uppdaterat.");
        printDetails(pokemon);
    }

    public static void editPokemonMaxHp(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        int hp = InputHelper.readIntInRange(
                scanner,
                "Nytt max HP: ",
                1,
                999
        );

        Pokemon updatedPokemon = PokemonService.editPokemonMaxHp(
                pokemons,
                pokemon,
                hp
        );

        System.out.println("Max HP uppdaterades.");
        printDetails(updatedPokemon);
    }

    public static void removePokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        System.out.println("Ange namn på den Pokemon du vill ta bort:");
        String name = scanner.nextLine();

        Pokemon pokemon = PokemonService.findPokemonByName(pokemons, name);

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

        if (confirm) {
            PokemonService.removePokemon(pokemons, pokemon);

            System.out.println(pokemon.getName() + " har tagits bort.");
        } else {
            System.out.println("Borttagningen avbröts.");
        }
    }

    // Söker efter en Pokemon med hjälp av namn.
    public static void searchPokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        System.out.println("Skriv namnet på en Pokemon:");
        String name = scanner.nextLine();

        Pokemon pokemon = PokemonService.findPokemonByName(
                pokemons,
                name
        );

        if (pokemon == null) {
            System.out.println("Pokemon hittades inte.");
            return;
        }

        printDetails(pokemon);
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
}
