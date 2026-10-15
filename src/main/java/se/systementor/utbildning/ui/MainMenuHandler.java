package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.PokemonLoadException;
import se.systementor.utbildning.exception.PokemonSaveException;
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
    private final PokemonFileService pokemonFileService;
    private boolean running = true;

    // Tar emot sökvägar när menyobjektet skapas.
    public MainMenuHandler(
            Path path,
            Path seedPath,
            PokemonFileService pokemonFileService
    ) {
        this.path = path;
        this.seedPath = seedPath;

        // Skapar beroendena och kopplar ihop spelmenyn.
        BattleStats stats = BattleStatsFileService.loadStats();
        CpuPlayer cpuPlayer = new CpuPlayer(new AggressiveStrategy());

        this.battleService = new BattleService(stats, cpuPlayer);
        // Återanvänder filhanteraren som skapats i Main.
        this.pokemonFileService = pokemonFileService;

        PokemonService pokemonService = new PokemonService(
                stats,
                this.pokemonFileService
        );

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
            case 1 -> pokemonMenu.showAllPokemons(pokemons);

            case 2 -> playMenuHandler.show(pokemons, scanner);

            case 3 -> pokemonMenu.searchPokemon(pokemons, scanner);

            // Skickar vidare statistiken så att Pokémon-ID:n inte återanvänds.
            case 4 -> pokemonMenu.addPokemon(pokemons, scanner);

            // Använder Pokemon-menyn som skapades via konstruktorn.
            case 5 -> pokemonMenu.editPokemon(pokemons, scanner);

            case 6 -> pokemonMenu.removePokemon(pokemons, scanner);

            case 7 -> pokemonFileService.savePokemonsToFile(pokemons);

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
