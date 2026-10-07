package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;
import se.systementor.utbildning.service.AttackService;
import se.systementor.utbildning.service.PokemonService;

import java.util.List;
import java.util.Scanner;

public class PokemonMenu {

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
        PokemonService.printDetails(pokemon);
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
            case 6 -> AttackService.removeAttackFromPokemon(pokemon, scanner);
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

        PokemonService.editPokemonName(
                pokemons,
                pokemon,
                newName
        );
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
        PokemonService.printDetails(updatedPokemon);
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
        PokemonService.printDetails(pokemon);
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
        PokemonService.printDetails(updatedPokemon);
    }
}
