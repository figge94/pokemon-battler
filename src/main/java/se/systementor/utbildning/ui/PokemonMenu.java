package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.InvalidPokemonException;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;
import se.systementor.utbildning.model.BattleStats;

import se.systementor.utbildning.repository.PokemonFileService;

import se.systementor.utbildning.service.PokemonService;

import java.util.List;
import java.util.Scanner;

public class PokemonMenu {

    public static void addPokemon(
            List<Pokemon> pokemons,
            Scanner scanner,
            BattleStats stats
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

        int speed = InputHelper.readIntInRange(
                scanner,
                "Snabbhet: ",
                1,
                200
        );

        Pokemon pokemon = PokemonService.createPokemon(
                pokemons,
                name,
                hp,
                type,
                speed,
                stats
        );

        ConsoleUI.header("FÖRHANDSVISNING");
        printDetails(pokemon);

        boolean confirm = InputHelper.readYesNo(
                scanner,
                "\nBekräfta genom att skriva ja eller nej: "
        );

        if (confirm) {
            PokemonService.addPokemon(pokemons, pokemon);

            PokemonFileService.savePokemonsToFile(pokemons);

            Messages.success("Pokemon tillagd och sparad.");
        } else {
            Messages.warning("Avbrutet.");
        }
    }

    // Visar redigeringsmenyn och skickar vidare till rätt metod.
    public static void editPokemon(List<Pokemon> pokemons, Scanner scanner) {
        Messages.info("Ange namn på den Pokemon du vill redigera:");
        String name = scanner.nextLine();

        // Hämtar vald Pokemon innan redigeringsmenyn visas.
        Pokemon pokemon = PokemonService.findPokemonByName(pokemons, name);

        if (pokemon == null) {
            Messages.error(name + " hittades inte.");
            return;
        }

        ConsoleUI.header("FÖRHANDSVISNING");
        printDetails(pokemon);
        System.out.println();

        Menu.showEditMenu();

        int choice = InputHelper.readIntInRange(scanner, "Välj: ", 1, 8);

        // Skickar användaren vidare till rätt redigeringsmetod beroende på valet.
        switch (choice) {
            case 1 -> editPokemonName(pokemons, pokemon, scanner);
            case 2 -> editPokemonType(pokemons, pokemon, scanner);
            case 3 -> editPokemonCurrentHp(pokemon, scanner);
            case 4 -> editPokemonMaxHp(pokemons, pokemon, scanner);
            case 5 -> editPokemonSpeed(pokemons, pokemon, scanner);
            case 6 -> AttackMenu.handleAddAttack(pokemon, scanner);
            case 7 -> AttackMenu.removeAttackFromPokemon(pokemon, scanner);
            case 8 -> Messages.info("Gå tillbaka till huvudmenyn.");
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

            ConsoleUI.header("FÖRHANDSVISNING");
            printDetails(updatedPokemon);

        } catch (InvalidPokemonException e) {
            Messages.error(e.getMessage());
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

        Messages.success("Typen är uppdaterad.");
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

        Messages.success("Nuvarande HP är uppdaterat.");
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

        Messages.success("Max HP uppdaterades.");
        printDetails(updatedPokemon);
    }

    public static void editPokemonSpeed(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        int speed = InputHelper.readIntInRange(
                scanner,
                "Ny snabbhet: ",
                1,
                200
        );

        Pokemon updatedPokemon = PokemonService.editPokemonSpeed(
                pokemons,
                pokemon,
                speed
        );

        Messages.success("Snabbheten uppdaterades.");
        printDetails(updatedPokemon);
    }

    public static void removePokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        Messages.info("Ange namn på den Pokemon du vill ta bort:");
        String name = scanner.nextLine();

        Pokemon pokemon = PokemonService.findPokemonByName(pokemons, name);

        if (pokemon == null) {
            Messages.error("Pokemon hittades inte.");
            return;
        }

        Messages.warning("VARNING! Du är på väg att ta bort:");
        Messages.error(pokemon.getId() + ". " + pokemon.getName());

        boolean confirm = InputHelper.readYesNo(
                scanner,
                "Bekräfta genom att skriva ja eller nej: "
        );

        if (confirm) {
            PokemonService.removePokemon(pokemons, pokemon);
            PokemonFileService.savePokemonsToFile(pokemons);

            Messages.success(pokemon.getName() + " har tagits bort.");
        } else {
            Messages.warning("Borttagningen avbröts.");
        }
    }

    // Söker efter en Pokemon med hjälp av namn.
    public static void searchPokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        Messages.info("Skriv namnet på en Pokemon:");
        String name = scanner.nextLine();

        Pokemon pokemon = PokemonService.findPokemonByName(
                pokemons,
                name
        );

        if (pokemon == null) {
            Messages.error("Pokemon hittades inte.");
            return;
        }

        ConsoleUI.header("SÖKRESULTAT");
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
        System.out.print(pokemon);
    }
}
