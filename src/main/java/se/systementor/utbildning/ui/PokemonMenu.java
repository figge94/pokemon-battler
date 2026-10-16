package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.PokemonSaveException;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

import se.systementor.utbildning.service.PokemonService;

import java.util.List;
import java.util.Scanner;

// Hanterar Pokemon-menyn och skickar redigering vidare till PokemonEditMenu.
public class PokemonMenu {
    private final PokemonService pokemonService;
    private final PokemonEditMenu pokemonEditMenu;

    // Tar emot PokemonService via Dependency Injection.
    public PokemonMenu(PokemonService pokemonService) {
        this.pokemonService = pokemonService;

        AttackMenu attackMenu = new AttackMenu(pokemonService);
        this.pokemonEditMenu = new PokemonEditMenu(pokemonService, attackMenu);
    }

    // Läser in uppgifter och låter användaren bekräfta en ny Pokemon.
    public void addPokemon(
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

        int speed = InputHelper.readIntInRange(
                scanner,
                "Snabbhet: ",
                1,
                200
        );

        Pokemon pokemon = pokemonService.createPokemon(
                pokemons,
                name,
                hp,
                type,
                speed
        );

        ConsoleUI.header("FÖRHANDSVISNING");
        printDetails(pokemon);

        boolean confirm = InputHelper.readYesNo(
                scanner,
                "\nBekräfta genom att skriva ja eller nej: "
        );

        if (confirm) {
            try {
                pokemonService.addPokemon(pokemons, pokemon);
                Messages.success("Pokemon tillagd och sparad.");

            } catch (PokemonSaveException e) {
                Messages.error(e.getMessage());
            }
        } else {
            Messages.warning("Avbrutet.");
        }
    }

    // Skickar redigeringen vidare till PokemonEditMenu.
    public void editPokemon(List<Pokemon> pokemons, Scanner scanner) {
        pokemonEditMenu.editPokemon(pokemons, scanner);
    }

    // Söker efter en Pokemon och kräver bekräftelse innan borttagning.
    public void removePokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        Messages.info("Ange namn på den Pokemon du vill ta bort:");
        String name = scanner.nextLine().trim();

        Pokemon pokemon = pokemonService.findPokemonByName(pokemons, name);

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
            try {
                pokemonService.removePokemon(pokemons, pokemon);
                Messages.success(pokemon.getName() + " har tagits bort.");
            } catch (PokemonSaveException e) {
                Messages.error(e.getMessage());
            }
        } else {
            Messages.warning("Borttagningen avbröts.");
        }
    }

    // Söker efter en Pokemon med Optional.
    public void searchPokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        // Läser in namnet på den Pokemon som ska sökas efter.
        Messages.info("Skriv namn på en Pokemon:");
        String name = scanner.nextLine().trim();

        // Visar Pokemon om den hittas, annars ett felmeddelande.
        pokemonService.findPokemonOptional(pokemons, name)
                .ifPresentOrElse(
                        pokemon -> {
                            ConsoleUI.header("SÖKRESULTAT");
                            printDetails(pokemon);
                        },
                        () -> Messages.error("Pokemon hittades inte.")
                );
    }

    // Visar alla Pokemon och deras attacker.
    public void showAllPokemons(List<Pokemon> pokemons) {
        for (Pokemon pokemon : pokemons) {
            printDetails(pokemon);
            System.out.println();
        }
    }

    // Använder PokemonFormatter för att skriva ut Pokemons information.
    public static void printDetails(Pokemon pokemon) {
        System.out.print(PokemonFormatter.format(pokemon));
    }
}
