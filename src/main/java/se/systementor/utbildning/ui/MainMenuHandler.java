
package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.PokemonLoadException;
import se.systementor.utbildning.exception.PokemonSaveException;

import se.systementor.utbildning.model.Pokemon;

import se.systementor.utbildning.repository.PokemonFileService;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

// Ansvarar för programmets huvudmeny och användarens menyval.
// Skickar vidare arbetet till andra klasser beroende på vad användaren väljer.
// Själva filhanteringen utförs av PokemonFileService.
public class MainMenuHandler {

    // Sökvägar till programmets datafiler.
    private final Path seedPath;

    // Referenser till de objekt som hanterar programmets olika delar.
    // Beroendena skickas in via konstruktorn för att återanvändas.
    private final PokemonMenu pokemonMenu;
    private final PlayMenuHandler playMenuHandler;
    private final PokemonFileService pokemonFileService;

    // Håller reda på om programmets huvudloop ska fortsätta.
    private boolean running = true;


    // Tar emot beroenden och sökvägen till seed-filen.
    // PokemonFileService ansvarar för den vanliga sparfilens sökväg.
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


    // Returnerar om huvudmenyn fortfarande ska köras.
    // Main kan använda detta för att styra programmets huvudloop.
    public boolean isRunning() {
        return running;
    }

    // Visar huvudmenyn, hanterar användarens val
    // och returnerar den aktuella listan med Pokemon.
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

        // Skickar vidare till rätt funktion utifrån användarens val.
        // Huvudmenyn behöver inte själv utföra logiken i varje funktion.
        switch (choice) {

            // Visar alla Pokemon som finns i den aktuella listan.
            case 1 -> pokemonMenu.showAllPokemons(pokemons);

            // Öppnar spelmenyn där användaren kan starta en strid.
            case 2 -> playMenuHandler.show(pokemons, scanner);

            // Söker efter en Pokemon genom PokemonMenu.
            case 3 -> pokemonMenu.searchPokemon(pokemons, scanner);

            // Låter användaren skapa och lägga till en ny Pokemon.
            case 4 -> pokemonMenu.addPokemon(pokemons, scanner);

            // Öppnar redigeringsmenyn för en befintlig Pokemon.
            case 5 -> pokemonMenu.editPokemon(pokemons, scanner);

            // Låter användaren ta bort en Pokemon från listan.
            case 6 -> pokemonMenu.removePokemon(pokemons, scanner);

            // Sparar den aktuella Pokemon-listan till JSON-filen.
            case 7 -> {
                try {
                    // Repository ansvarar för själva sparandet till fil.
                    pokemonFileService.savePokemonsToFile(pokemons);

                    // UI visar ett meddelande när sparandet lyckas.
                    Messages.success("Pokémon-data har sparats.");

                } catch (PokemonSaveException e) {
                    // Visar ett felmeddelande om filen inte kunde sparas.
                    Messages.error(e.getMessage());
                }
            }

            // Läser in tidigare sparad Pokemon-data från JSON-filen.
            case 8 -> {

                // Kontrollerar först om det finns en sparad fil att läsa.
                if (!pokemonFileService.saveFileExists()) {
                    Messages.warning("Ingen sparad Pokémon-fil hittades.");

                } else {
                    try {
                        // Ersätter den aktuella listan med data från filen.
                        // PokemonFileService ansvarar för inläsningen.
                        pokemons = pokemonFileService.loadPokemons();

                        Messages.success("Pokémon-data har laddats.");

                    } catch (PokemonLoadException e) {
                        // Om inläsningen misslyckas visas felet.
                        // Den tidigare Pokemon-listan behålls.
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
                // Försöker spara Pokemon-listan innan programmet avslutas.
                try {
                    pokemonFileService.savePokemonsToFile(pokemons);
                    Messages.success("Pokémon-data har sparats.");

                    // Avslutar huvudloopen när sparandet har lyckats.
                    System.out.println("Avslutar...");
                    running = false;

                } catch (PokemonSaveException e) {
                    // Visar felet och låter huvudmenyn fortsätta.
                    // Användaren kan då försöka spara igen.
                    Messages.error(e.getMessage());
                }
            }

        }

        // Returnerar listan eftersom den kan ha ersatts
        // vid inläsning eller återställning.
        return pokemons;
    }
}
