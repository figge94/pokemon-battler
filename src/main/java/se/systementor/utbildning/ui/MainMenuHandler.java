package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.repository.PokemonFileService;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.CpuPlayer;
import se.systementor.utbildning.service.BattleService;
import se.systementor.utbildning.repository.BattleStatsFileService;
import se.systementor.utbildning.service.PokemonService;
import se.systementor.utbildning.strategy.AggressiveStrategy;

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
    private final BattleService battleService;
    private boolean running = true;

    // Tar emot sökvägar när menyobjektet skapas.
    public MainMenuHandler(Path path, Path seedPath) {
        this.path = path;
        this.seedPath = seedPath;

        // Skapar beroendena och kopplar ihop spelmenyn.
        BattleStats stats = BattleStatsFileService.loadStats();
        CpuPlayer cpuPlayer = new CpuPlayer(new AggressiveStrategy());

        this.battleService = new BattleService(stats, cpuPlayer);
        PokemonService pokemonService = new PokemonService(stats);
        this.pokemonMenu = new PokemonMenu(pokemonService);
        this.playMenuHandler = new PlayMenuHandler(this.battleService);
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
            case 1 -> PokemonMenu.showAllPokemons(pokemons);

            case 2 -> playMenuHandler.show(pokemons, scanner);

            case 3 -> pokemonMenu.searchPokemon(pokemons, scanner);

            // Skickar vidare statistiken så att Pokémon-ID:n inte återanvänds.
            case 4 -> pokemonMenu.addPokemon(pokemons, scanner);

            // Använder Pokemon-menyn som skapades via konstruktorn.
            case 5 -> pokemonMenu.editPokemon(pokemons, scanner);

            case 6 -> pokemonMenu.removePokemon(pokemons, scanner);

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
