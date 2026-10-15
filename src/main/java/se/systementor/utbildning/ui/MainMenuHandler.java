package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.repository.PokemonFileService;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class MainMenuHandler {

    // Varje menyobjekt håller reda på sitt eget tillstånd.
    private boolean running = true;

    public boolean isRunning() {
        return running;
    }

    // Hanterar huvudmenyn och returnerar aktuell Pokemon-lista.
    public List<Pokemon> show(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        Path path = Path.of("pokemon.json");
        Path seedPath = Path.of("pokemon-seed.json");

        Menu.showMainMenu();

        int choice = InputHelper.readIntInRange(
                scanner,
                "\nVad vill du göra? ",
                1,
                10
        );

        switch (choice) {
            case 1 -> PokemonMenu.showAllPokemons(pokemons);

            case 2 -> PlayMenuHandler.show(pokemons, scanner);

            case 3 -> PokemonMenu.searchPokemon(pokemons, scanner);

            case 4 -> PokemonMenu.addPokemon(pokemons, scanner);

            case 5 -> PokemonMenu.editPokemon(pokemons, scanner);

            case 6 -> PokemonMenu.removePokemon(pokemons, scanner);

            case 7 -> PokemonFileService.savePokemonsToFile(pokemons);

            case 8 -> pokemons =
                    PokemonFileService.loadPokemons(path, pokemons);

            case 9 -> pokemons =
                    PokemonFileService.resetPokemons(
                            scanner, pokemons, seedPath
                    );

            case 10 -> {
                // Sparar och avslutar huvudloopen.
                PokemonFileService.savePokemonsToFile(pokemons);
                System.out.println("Avslutar...");
                running = false;
            }
        }

        return pokemons;
    }
}
