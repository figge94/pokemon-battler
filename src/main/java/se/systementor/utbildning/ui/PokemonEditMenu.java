package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.InvalidPokemonException;
import se.systementor.utbildning.exception.PokemonSaveException;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

import se.systementor.utbildning.service.PokemonService;

import java.util.List;
import java.util.Scanner;

// Hanterar redigering av en Pokemons egenskaper och attacker.
public class PokemonEditMenu {

    private final PokemonService pokemonService;
    private final AttackMenu attackMenu;

    // Tar emot beroenden via konstruktorn.
    public PokemonEditMenu(
            PokemonService pokemonService,
            AttackMenu attackMenu
    ) {
        this.pokemonService = pokemonService;
        this.attackMenu = attackMenu;
    }

    // Låter användaren redigera samma Pokemon tills man går tillbaka.
    public void editPokemon(List<Pokemon> pokemons, Scanner scanner) {
        Messages.info("Ange namn på den Pokemon du vill redigera:");
        String name = scanner.nextLine().trim();

        Pokemon pokemon = pokemonService.findPokemonOptional(pokemons, name)
                .orElse(null);

        if (pokemon == null) {
            Messages.error(name + " hittades inte.");
            return;
        }

        // Sparar ID eftersom namnet kan ändras.
        int pokemonId = pokemon.getId();

        boolean editing = true;

        while (editing) {

            // Hämtar den senaste versionen av Pokemon från listan.
            pokemon = null;

            for (Pokemon current : pokemons) {
                if (current.getId() == pokemonId) {
                    pokemon = current;
                    break;
                }
            }

            if (pokemon == null) {
                Messages.error("Pokemon hittades inte längre.");
                return;
            }

            ConsoleUI.header("AKTUELL POKEMON");
            PokemonMenu.printDetails(pokemon);
            System.out.println();

            Menu.showEditMenu();

            int choice = InputHelper.readIntInRange(
                    scanner, "Välj: ", 1, 8
            );

            switch (choice) {
                case 1 -> editPokemonName(pokemons, pokemon, scanner);
                case 2 -> editPokemonType(pokemons, pokemon, scanner);
                case 3 -> editPokemonCurrentHp(pokemons, pokemon, scanner);
                case 4 -> editPokemonMaxHp(pokemons, pokemon, scanner);
                case 5 -> editPokemonSpeed(pokemons, pokemon, scanner);
                case 6 -> attackMenu.handleAddAttack(pokemons, pokemon, scanner);
                case 7 -> attackMenu.removeAttackFromPokemon(
                        pokemons, pokemon, scanner
                );
                case 8 -> {
                    Messages.info("Går tillbaka till huvudmenyn.");
                    editing = false;
                }
            }
        }
    }

    // Ändrar Pokemons namn.
    private void editPokemonName(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        String newName = InputHelper.readValidName(
                scanner,
                "Nytt namn: "
        );

        try {
            pokemonService.editPokemonName(
                    pokemons,
                    pokemon,
                    newName
            );

            Messages.success("Namnet har uppdaterats.");

        } catch (InvalidPokemonException | PokemonSaveException e) {
            Messages.error(e.getMessage());
        }
    }

    // Ändrar Pokemons typ.
    private void editPokemonType(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        Type newType = InputHelper.chooseType(scanner);

        try {
            pokemonService.editPokemonType(
                    pokemons,
                    pokemon,
                    newType
            );

            Messages.success("Typen har uppdaterats.");

        } catch (PokemonSaveException e) {
            Messages.error(e.getMessage());
        }
    }

    // Ändrar Pokemons nuvarande HP.
    private void editPokemonCurrentHp(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        int hp = InputHelper.readIntInRange(
                scanner,
                "Nytt nuvarande HP: ",
                0,
                pokemon.getMaxHp()
        );

        try {
            pokemonService.editPokemonCurrentHp(
                    pokemons,
                    pokemon,
                    hp
            );

            Messages.success("Nuvarande HP har uppdaterats.");

        } catch (PokemonSaveException e) {
            Messages.error(e.getMessage());
        }
    }

    // Ändrar Pokemons maximala HP.
    private void editPokemonMaxHp(
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

        try {
            pokemonService.editPokemonMaxHp(
                    pokemons,
                    pokemon,
                    hp
            );

            Messages.success("MaxHP har uppdaterats.");

        } catch (PokemonSaveException e) {
            Messages.error(e.getMessage());
        }
    }

    // Ändrar Pokemons snabbhet.
    private void editPokemonSpeed(
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

        try {
            pokemonService.editPokemonSpeed(
                    pokemons,
                    pokemon,
                    speed
            );

            Messages.success("Snabbheten har uppdaterats.");

        } catch (PokemonSaveException e) {
            Messages.error(e.getMessage());
        }
    }
}
