package se.systementor.utbildning.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import se.systementor.utbildning.model.BattleStats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Ansvarar för att spara och läsa stridsstatistik från en JSON-fil.
// Genom att samla filhanteringen här behöver andra klasser
// inte känna till hur statistiken lagras på datorn.
public class BattleStatsFileService {

    // Anger vilken fil som används för att lagra stridsstatistiken.
    // static final innebär att samma sökväg används av alla metodanrop.
    private static final Path FILE_PATH = Path.of("battle-stats.json");

    // Jacksons ObjectMapper omvandlar Java-objekt till JSON och tillbaka.
    // Samma instans återanvänds vid både inläsning och sparande.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Sparar den aktuella stridsstatistiken till JSON-filen.
    public static void saveStats(BattleStats stats) {

        // Försöker omvandla BattleStats till JSON och skriva till filen.
        try {
            // PrettyPrinter gör JSON-filen mer lättläst.
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(FILE_PATH.toFile(), stats);

        } catch (IOException e) {
            // Hanterar fel vid sparandet och visar ett meddelande
            // i stället för att låta ett IOException avsluta programmet.
            System.out.println(
                    "Kunde inte spara stridsstatistiken."
            );
        }
    }

    // Läser in tidigare sparad stridsstatistik från JSON-filen.
    // Returnerar ett nytt statistikobjekt om filen saknas eller inte kan läsas.
    public static BattleStats loadStats() {

        // Kontrollerar om statistikfilen finns innan den läses.
        // Vid första programstarten finns normalt ingen sparad statistik.
        if (!Files.exists(FILE_PATH)) {
            return new BattleStats();
        }

        // Försöker läsa JSON-filen och skapa ett BattleStats-objekt.
        try {
            // Jackson omvandlar JSON till objekt av typen BattleStats.
            return objectMapper.readValue(
                    FILE_PATH.toFile(),
                    BattleStats.class
            );

        } catch (IOException e) {
            // Hanterar exempelvis trasig JSON eller problem med filåtkomst.
            // Programmet kan fortsätta med ett nytt statistikobjekt.
            System.out.println(
                    "Kunde inte läsa statistiken. Ny statistik skapas."
            );

            return new BattleStats();
        }
    }
}