
package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.PokemonLoadException;
import se.systementor.utbildning.exception.PokemonSaveException;

import se.systementor.utbildning.model.Pokemon;

import se.systementor.utbildning.repository.PokemonFileService;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

// Hanterar huvudmenyn och skickar vidare användarens val till rätt klass.
public class MainMenuHandler {
    private final Path seedPath;

    private final PokemonMenu pokemonMenu;
    private final PlayMenuHandler playMenuHandler;
    private final PokemonFileService pokemonFileService;

    // Styr om huvudmenyn ska fortsätta visas.
    private boolean running = true;

    // Tar emot beroenden via konstruktorn.
    public MainMenuHandler(
            Path seedPath,
            PokemonFileService pokemonFileService,
            PokemonMenu pokemonMenu,
            PlayMenuHandler playMenuHandler
    ) {
        this.seedPath = seedPath;
        this.pokemonFileService = pokemonFileService;
        this.pokemonMenu = pokemonMenu;
        this.playMenuHandler = playMenuHandler;
    }

    // Anger om huvudmenyn fortfarande ska köras.
    public boolean isRunning() {
        return running;
    }

    // Sparar Pokemon-listan och returnerar om det lyckades.
    private boolean savePokemons(List<Pokemon> pokemons) {
        try {
            pokemonFileService.savePokemonsToFile(pokemons);
            Messages.success("Pokemon-data har sparats.");
            return true;

        } catch (PokemonSaveException e) {
            Messages.error(e.getMessage());
            return false;
        }
    }

    // Visar huvudmenyn och returnerar den aktuella Pokemon-listan.
    public List<Pokemon> show(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        // Skriver ut huvudmenyn och läser ett giltigt menyval.
        // InputHelper ansvarar för att kontrollera inmatningen.
        Menu.showMainMenu();

        int choice = InputHelper.readIntInRange(
                scanner,
                "\nVad vill du göra? ",
                1,
                10
        );

        // Hanterar användarens menyval.
        switch (choice) {
            case 1 -> pokemonMenu.showAllPokemons(pokemons);
            case 2 -> playMenuHandler.show(pokemons, scanner);
            case 3 -> pokemonMenu.searchPokemon(pokemons, scanner);
            case 4 -> pokemonMenu.addPokemon(pokemons, scanner);
            case 5 -> pokemonMenu.editPokemon(pokemons, scanner);
            case 6 -> pokemonMenu.removePokemon(pokemons, scanner);
            case 7 -> savePokemons(pokemons);

            // Läser in sparad Pokemon-data.
            case 8 -> {
                if (!pokemonFileService.saveFileExists()) {
                    Messages.warning("Ingen sparad Pokemon-fil hittades.");

                } else {
                    try {
                        // Ersätter listan med sparad data.
                        pokemons = pokemonFileService.loadPokemons();
                        Messages.success("Pokemon-data har laddats.");

                    } catch (PokemonLoadException e) {
                        // Behåller den tidigare listan vid fel.
                        Messages.error(e.getMessage());
                    }
                }
            }

            // Återställer Pokemon-listan till programmets seedade data.
            case 9 -> {

                // Kräver bekräftelse eftersom återställningen
                // ersätter användarens tidigare sparade Pokemon-data.
                boolean confirm = InputHelper.readYesNo(
                        scanner,
                        "Är du säker på att du vill återställa all data? ja/nej: "
                );

                if (confirm) {
                    try {
                        // Repository läser in seed-filen och sparar
                        // den återställda informationen.
                        pokemons = pokemonFileService.resetPokemons(seedPath);

                        Messages.success("Data återställd till seedad data.");

                    } catch (PokemonLoadException | PokemonSaveException e) {
                        // Hanterar både fel vid inläsning och sparande.
                        Messages.error(e.getMessage());
                    }
                }
            }

            case 10 -> {
                // Avslutar bara programmet om sparandet lyckas.
                if (savePokemons(pokemons)) {
                    System.out.println("Avslutar...");
                    running = false;
                }
            }
        }

        // Returnerar listan eftersom den kan ha ersatts
        // vid inläsning eller återställning.
        return pokemons;
    }
}
