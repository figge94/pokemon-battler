package se.systementor.utbildning.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import se.systementor.utbildning.model.BattleStats;

import se.systementor.utbildning.exception.BattleStatsSaveException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Ansvarar för att spara och läsa stridsstatistik från en JSON-fil.
public class BattleStatsFileService {

    // static final gör att alla anrop använder samma filsökväg.
    private static final Path FILE_PATH = Path.of("battle-stats.json");

    // Jacksons ObjectMapper omvandlar Java-objekt till JSON och tillbaka.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Sparar stridsstatistiken och skickar vidare fel om sparandet misslyckas.
    public static void saveStats(BattleStats stats) {
        try {
            // PrettyPrinter gör JSON-filen mer lättläst.
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(FILE_PATH.toFile(), stats);

        } catch (IOException e) {
            throw new BattleStatsSaveException(
                    "Kunde inte spara stridsstatistiken.",
                    e
            );
        }
    }

    // Läser statistik från JSON eller skapar ny om filen saknas eller inte kan läsas.
    public static BattleStats loadStats() {

        // Vid första programstarten finns ingen statistikfil.
        if (!Files.exists(FILE_PATH)) {
            return new BattleStats();
        }

        try {
            // Jackson omvandlar JSON till ett BattleStats-objekt.
            return objectMapper.readValue(
                    FILE_PATH.toFile(),
                    BattleStats.class
            );

        } catch (IOException e) {
            // Programmet fortsätter med tom statistik om inläsningen misslyckas.
            System.out.println(
                    "Kunde inte läsa statistiken. Ny statistik skapas."
            );

            return new BattleStats();
        }
    }
}