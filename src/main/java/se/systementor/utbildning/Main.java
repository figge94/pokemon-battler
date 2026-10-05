package se.systementor.utbildning;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    // Startar programmet och hanterar huvudmenyn
    public static void main(String[] args) {
        // Skapar Scanner för att läsa användarens input.
        Scanner scanner = new Scanner(System.in);
        // Styr om huvudmenyn ska fortsätta köras.
        boolean running = true;

        // Sökvägar till den vanliga JSON-filen och seed-filen.
        Path path = Path.of("pokemon.json");
        Path seedPath = Path.of("pokemon-seed.json");

        // Läser in Pokemon från fil när programmet startar.
        List<Pokemon> pokemons =
                PokemonFileService.loadInitialPokemons(path, seedPath);

        // Huvudloopen körs tills användaren väljer att avsluta.
        while (running) {
            Menu.showMainMenu();

            int choice = InputHelper.readIntInRange(
                    scanner,
                    "\nVad vill du göra? ",
                    1,
                    9
            );

            switch (choice) {
                case 1:
                    // Visar alla Pokemon.
                    PokemonService.showAllPokemons(pokemons);
                    break;

                case 2:
                    // Spela
                    Menu.showPlayMenu();
                    break;

                case 3:
                    // Söker efter en Pokemon.
                    PokemonService.searchPokemon(pokemons, scanner);
                    break;

                case 4:
                    // Lägger till en Pokemon.
                    PokemonService.addPokemon(pokemons, scanner);
                    break;

                case 5:
                    // Redigerar en Pokemon.
                    PokemonService.editPokemon(pokemons, scanner);
                    break;

                case 6:
                    // Tar bort en pokemon.
                    PokemonService.removePokemon(pokemons, scanner);
                    break;

                case 7:
                    // Sparar alla Pokemon till en JSON-filen.
                    PokemonFileService.savePokemonsToFile(pokemons);
                    break;

                case 8:
                    // Laddar Pokemon från fil.
                    pokemons = PokemonFileService.loadPokemons(path, pokemons);
                    break;

                case 9:
                    // Återställer till seedad data.
                    pokemons = PokemonFileService.resetPokemons(scanner, pokemons, seedPath);
                    break;

                case 10:
                    // Sparar automatiskt innan programmet avslutas.
                    PokemonFileService.savePokemonsToFile(pokemons);
                    System.out.println("Avslutar...");
                    running = false;
                    break;

                default:
                    System.out.println("Ogiltigt val. Försök igen!");
            }
        }

        // Stänger scanner när programmet avslutas.
        scanner.close();
    }
}
