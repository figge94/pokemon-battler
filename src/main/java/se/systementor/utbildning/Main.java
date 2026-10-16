
package se.systementor.utbildning;

import se.systementor.utbildning.repository.PokemonFileService;
import se.systementor.utbildning.repository.BattleStatsFileService;
import se.systementor.utbildning.exception.PokemonLoadException;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.CpuPlayer;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.service.BattleService;
import se.systementor.utbildning.service.PokemonService;
import se.systementor.utbildning.strategy.RandomStrategy;
import se.systementor.utbildning.ui.PokemonMenu;
import se.systementor.utbildning.ui.PlayMenuHandler;
import se.systementor.utbildning.ui.Messages;
import se.systementor.utbildning.ui.MainMenuHandler;

import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Anger var sparad Pokemon-data och startdata finns.
        Path path = Path.of("pokemon.json");
        Path seedPath = Path.of("pokemon-seed.json");

        // Skapar filhanteraren som ansvarar för att läsa och spara Pokemon.
        // Samma objekt används av flera delar av programmet.
        PokemonFileService pokemonFileService = new PokemonFileService(path);

        // Läser in tidigare stridsstatistik eller skapar tom statistik.
        BattleStats stats = BattleStatsFileService.loadStats();

        // Skapar CPU-spelaren med en slumpmässig strategi.
        // Strategin skickas in via konstruktorn med Dependency Injection.
        CpuPlayer cpuPlayer = new CpuPlayer(new RandomStrategy());

        // Skapar stridshanteraren med statistik och CPU-spelare.
        // BattleService får sina beroenden i stället för att skapa dem själv.
        BattleService battleService = new BattleService(stats, cpuPlayer);

        // Skapar menyn för strider och statistik.
        PlayMenuHandler playMenuHandler = new PlayMenuHandler(battleService);

        // Skapar tjänsten som hanterar Pokemon och deras ändringar.
        // Samma statistik och filhanterare används genom hela programmet.
        PokemonService pokemonService = new PokemonService(
                stats,
                pokemonFileService
        );

        // Skapar menyn för att visa, lägga till och redigera Pokemon.
        PokemonMenu pokemonMenu = new PokemonMenu(pokemonService);

        // Skapar huvudmenyn med dess beroenden.
        // Seed-sökvägen används vid återställning av Pokémon-data.
        MainMenuHandler mainMenu = new MainMenuHandler(
                seedPath,
                pokemonFileService,
                pokemonMenu,
                playMenuHandler
        );

        // Laddar sparad Pokemon-data eller startdata vid programstart.
        List<Pokemon> pokemons;

        boolean saveFileExists = pokemonFileService.saveFileExists();

        try {
            pokemons = pokemonFileService.loadInitialPokemons(seedPath);

            if (!saveFileExists) {
                Messages.info("Inga sparade Pokémon finns. Startdata har laddats.");
            }

        } catch (PokemonLoadException e) {

            if (!saveFileExists) {
                // Även seed-filen kunde inte laddas.
                Messages.error(e.getMessage());
                return;
            }

            // Sparfilen kunde inte läsas, så vi försöker med startdata.
            Messages.error("Den sparade Pokemon-filen kunde inte läsas.");
            Messages.info("Startdata laddas i stället.");

            try {
                pokemons = pokemonFileService.loadSeedPokemons(seedPath);

            } catch (PokemonLoadException seedError) {
                // Programmet kan inte starta utan giltiga Pokemon.
                Messages.error(seedError.getMessage());
                return;
            }
        }

        // Skapar en Scanner som används för användarens inmatning.
        // try-with-resources ser till att den stängs när programmet avslutas.
        try (Scanner scanner = new Scanner(System.in)) {

            // MainMenuHandler ansvarar för menyflödet.
            // Main behöver därför inte känna till de olika menyvalen.
            while (mainMenu.isRunning()) {
                pokemons = mainMenu.show(pokemons, scanner);
            }

        } catch (NoSuchElementException e) {
            // Hanterar avbruten inmatning utan en ohanterad exception.
            System.out.println(
                    "\nInmatningen avbröts. Programmet avslutas."
            );
        }
    }
}
