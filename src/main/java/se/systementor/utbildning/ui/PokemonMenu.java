package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.InvalidPokemonException;
import se.systementor.utbildning.exception.PokemonSaveException;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

import se.systementor.utbildning.service.PokemonService;

import java.util.List;
import java.util.Scanner;

public class PokemonMenu {
    // Pokémon-menyn får sin service via konstruktorn.
    private final PokemonService pokemonService;
    private final AttackMenu attackMenu;

    public PokemonMenu(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
        this.attackMenu = new AttackMenu(pokemonService);
    }

    // Läser in uppgifter, visar en förhandsvisning och låter användaren bekräfta en ny Pokemon.
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
                // UI ansvarar för att visa felmeddelandet.
                Messages.error(e.getMessage());
            }
        } else {
            Messages.warning("Avbrutet.");
        }
    }

    // Hanterar redigering av en befintlig Pokemon.
    public void editPokemon(List<Pokemon> pokemons, Scanner scanner) {
        Messages.info("Ange namn på den Pokemon du vill redigera:");
        String name = scanner.nextLine();

        // Söker efter Pokemon genom PokemonService.
        // Om ingen hittas visas ett felmeddelande och metoden avslutas.
        Pokemon pokemon = pokemonService.findPokemonOptional(pokemons, name)
                .orElse(null);

        if (pokemon == null) {
            Messages.error(name + " hittades inte.");
            return;
        }

        // Visar den valda Pokemons nuvarande egenskaper.
        ConsoleUI.header("FÖRHANDSVISNING");
        printDetails(pokemon);
        System.out.println();

        // Visar redigeringsmenyn och läser in användarens val.
        Menu.showEditMenu();

        int choice = InputHelper.readIntInRange(scanner, "Välj: ", 1, 8);

        // Skickar vidare till rätt metod beroende på vad användaren vill ändra.
        // PokemonService ansvarar för själva ändringen och sparandet.
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
            case 8 -> Messages.info("Gå tillbaka till huvudmenyn.");
        }
    }

    // Läser in ett nytt namn och låter PokemonService uppdatera och spara Pokemon.
    public void editPokemonName(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        String newName = InputHelper.readValidName(
                scanner,
                "Nytt namn: "
        );

        try {
            Pokemon updatedPokemon = pokemonService.editPokemonName(
                    pokemons,
                    pokemon,
                    newName
            );

            ConsoleUI.header("FÖRHANDSVISNING");
            printDetails(updatedPokemon);

        } catch (InvalidPokemonException | PokemonSaveException e) {
            Messages.error(e.getMessage());
        }
    }

    // Låter användaren välja en ny typ och visar Pokemon efter ändringen.
    public void editPokemonType(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        Type newType = InputHelper.chooseType(scanner);

        try {
            Pokemon updatedPokemon = pokemonService.editPokemonType(
                    pokemons,
                    pokemon,
                    newType
            );

            Messages.success("Typen är uppdaterad.");
            printDetails(updatedPokemon);

        } catch (PokemonSaveException e) {
            Messages.error(e.getMessage());
        }
    }

    // Läser in nytt HP inom tillåtet intervall och visar det uppdaterade värdet.
    public void editPokemonCurrentHp(
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

            Messages.success("Nuvarande HP är uppdaterat.");
            printDetails(pokemon);

        } catch (PokemonSaveException e) {
            Messages.error(e.getMessage());
        }
    }

    // Ändrar Pokemonens maximala HP och visar resultatet.
    public void editPokemonMaxHp(
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
            Pokemon updatedPokemon = pokemonService.editPokemonMaxHp(
                    pokemons,
                    pokemon,
                    hp
            );

            Messages.success("Max HP uppdaterades.");
            printDetails(updatedPokemon);

        } catch (PokemonSaveException e) {
            Messages.error(e.getMessage());
        }
    }

    // Ändrar Pokemonens snabbhet och visar de uppdaterade egenskaperna.
    public void editPokemonSpeed(
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
            Pokemon updatedPokemon = pokemonService.editPokemonSpeed(
                    pokemons,
                    pokemon,
                    speed
            );

            Messages.success("Snabbheten uppdaterades.");
            printDetails(updatedPokemon);

        } catch (PokemonSaveException e) {
            Messages.error(e.getMessage());
        }
    }

    // Söker efter en Pokemon och kräver bekräftelse innan den tas bort.
    public void removePokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        Messages.info("Ange namn på den Pokemon du vill ta bort:");
        String name = scanner.nextLine();

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
                // UI visar felet om sparandet misslyckas.
                Messages.error(e.getMessage());
            }
        } else {
            Messages.warning("Borttagningen avbröts.");
        }
    }

    // Söker efter namn och hanterar både träff och uteblivet resultat med Optional.
    public void searchPokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        // Läser in namnet på den Pokemon som ska sökas efter.
        Messages.info("Skriv namnet på en Pokemon:");
        String name = scanner.nextLine();

        // Optional hanterar både när en Pokemon hittas
        // och när sökningen inte ger något resultat.
        pokemonService.findPokemonOptional(pokemons, name)
                .ifPresentOrElse(
                        pokemon -> {
                            // Visar Pokemons information när den hittas.
                            ConsoleUI.header("SÖKRESULTAT");
                            printDetails(pokemon);
                        },
                        () -> {
                            // Visar ett felmeddelande om ingen Pokemon hittas.
                            Messages.error("Pokemon hittades inte.");
                        }
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
