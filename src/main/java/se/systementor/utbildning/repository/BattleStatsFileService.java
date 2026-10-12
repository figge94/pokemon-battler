package se.systementor.utbildning.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import se.systementor.utbildning.model.BattleStats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BattleStatsFileService {

    private static final Path FILE_PATH = Path.of("battle-stats.json");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void saveStats(BattleStats stats) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(FILE_PATH.toFile(), stats);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Kunde inte spara stridsstatistiken.", e
            );
        }
    }

    public static BattleStats loadStats() {
        if (!Files.exists(FILE_PATH)) {
            return new BattleStats();
        }

        try {
            return objectMapper.readValue(
                    FILE_PATH.toFile(),
                    BattleStats.class
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Kunde inte läsa stridsstatistiken.", e
            );
        }
    }
}