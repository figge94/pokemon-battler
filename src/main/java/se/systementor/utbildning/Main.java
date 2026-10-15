
package se.systementor.utbildning;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.repository.PokemonFileService;
import se.systementor.utbildning.exception.PokemonLoadException;
import se.systementor.utbildning.ui.Messages;
import se.systementor.utbildning.ui.MainMenuHandler;

import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Path path = Path.of("pokemon.json");
        Path seedPath = Path.of("pokemon-seed.json");

        // Skapar en gemensam filhanterare.
        PokemonFileService pokemonFileService = new PokemonFileService(path);

        // Skickar samma filhanterare till huvudmenyn
        MainMenuHandler mainMenu = new MainMenuHandler(
                path,
                seedPath,
                pokemonFileService
        );

        // Laddar Pokemon vid programstart.
        List<Pokemon> pokemons;

        try {
            // Försöker läsa sparade Pokémon eller startdata.
            pokemons = pokemonFileService.loadInitialPokemons(seedPath);
        } catch (PokemonLoadException e) {
            // Visar felet och avbryter en osäker uppstart.
            Messages.error(e.getMessage());
            return;
        }

        // Informerar användaren när startdata används.
        if (!pokemonFileService.saveFileExists()) {
            Messages.info("Inga sparade Pokémon finns. Startdata har laddats.");
        }

        // Stänger Scanner automatiskt när programmet avslutas.
        try (Scanner scanner = new Scanner(System.in)) {

            // Använder menyobjektet för att styra huvudmenyn.
            while (mainMenu.isRunning()) {
                pokemons = mainMenu.show(pokemons, scanner);
            }

        } catch (NoSuchElementException e) {
            System.out.println(
                    "\nInmatningen avbröts. Programmet avslutas."
            );
        }
    }
}
