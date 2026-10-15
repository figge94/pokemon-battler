package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.PokemonLoadException;
import se.systementor.utbildning.exception.PokemonSaveException;

import se.systementor.utbildning.model.Pokemon;

import se.systementor.utbildning.repository.PokemonFileService;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class MainMenuHandler {
    // Filsökvägar som används av huvudmenyn.
    private final Path path;
    private final Path seedPath;
    private final PokemonMenu pokemonMenu;
    // Spelmenyn använder samma BattleService under hela programmet.
    private final PlayMenuHandler playMenuHandler;
    private final PokemonFileService pokemonFileService;
    private boolean running = true;

    // Tar emot sökvägar när menyobjektet skapas.
    public MainMenuHandler(
            Path path,
            Path seedPath,
            PokemonFileService pokemonFileService,
            PokemonMenu pokemonMenu,
            PlayMenuHandler playMenuHandler
    ) {
        this.path = path;
        this.seedPath = seedPath;
        this.pokemonFileService = pokemonFileService;
        this.pokemonMenu = pokemonMenu;
        this.playMenuHandler = playMenuHandler;
    }

    public boolean isRunning() {
        return running;
    }

    // Hanterar huvudmenyn och returnerar aktuell Pokemon-lista.
    public List<Pokemon> show(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        Menu.showMainMenu();

        int choice = InputHelper.readIntInRange(
                scanner,
                "\nVad vill du göra? ",
                1,
                10
        );

        switch (choice) {
            case 1 -> pokemonMenu.showAllPokemons(pokemons);

            case 2 -> playMenuHandler.show(pokemons, scanner);

            case 3 -> pokemonMenu.searchPokemon(pokemons, scanner);

            // Skickar vidare statistiken så att Pokémon-ID:n inte återanvänds.
            case 4 -> pokemonMenu.addPokemon(pokemons, scanner);

            // Använder Pokemon-menyn som skapades via konstruktorn.
            case 5 -> pokemonMenu.editPokemon(pokemons, scanner);

            case 6 -> pokemonMenu.removePokemon(pokemons, scanner);

            case 7 -> {
                try {
                    pokemonFileService.savePokemonsToFile(pokemons);
                    Messages.success("Pokémon-data har sparats.");
                } catch (PokemonSaveException e) {
                    Messages.error(e.getMessage());
                }
            }

            case 8 -> {
                if (!pokemonFileService.saveFileExists()) {
                    Messages.warning("Ingen sparad Pokémon-fil hittades.");
                } else {
                    try {
                        // Läser Pokemon från fil via repository.
                        pokemons = pokemonFileService.loadPokemons();
                        Messages.success("Pokémon-data har laddats.");
                    } catch (PokemonLoadException e) {
                        Messages.error(e.getMessage());
                    }
                }
            }

            case 9 -> {
                boolean confirm = InputHelper.readYesNo(
                        scanner,
                        "Är du säker på att du vill återställa all data? ja/nej: "
                );

                if (confirm) {
                    try {
                        // Repository återställer filen, UI visar resultatet.
                        pokemons = pokemonFileService.resetPokemons(seedPath);
                        Messages.success("Data återställd till seedad data.");
                    } catch (PokemonLoadException | PokemonSaveException e) {
                        Messages.error(e.getMessage());
                    }
                }
            }

            case 10 -> {
                // Sparar och avslutar huvudloopen.
                pokemonFileService.savePokemonsToFile(pokemons);
                System.out.println("Avslutar...");
                running = false;
            }
        }

        return pokemons;
    }
}
