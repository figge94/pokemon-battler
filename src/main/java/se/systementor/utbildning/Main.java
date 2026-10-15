
package se.systementor.utbildning;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.repository.PokemonFileService;
import se.systementor.utbildning.ui.MainMenuHandler;

import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Path path = Path.of("pokemon.json");
        Path seedPath = Path.of("pokemon-seed.json");

        // Skapar menyobjektet och skickar in sökvägarna.
        MainMenuHandler mainMenu = new MainMenuHandler(path, seedPath);

        // Laddar Pokémon vid programstart.
        List<Pokemon> pokemons =
                PokemonFileService.loadInitialPokemons(path, seedPath);

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
