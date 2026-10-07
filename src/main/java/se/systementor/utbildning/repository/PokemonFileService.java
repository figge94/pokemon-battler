package se.systementor.utbildning.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import se.systementor.utbildning.ui.InputHelper;
import se.systementor.utbildning.exception.PokemonLoadException;
import se.systementor.utbildning.exception.PokemonSaveException;
import se.systementor.utbildning.model.Pokemon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PokemonFileService {

    // Sparar listan med Pokemon till standardfilen pokemon.json.
    public static void savePokemonsToFile(List<Pokemon> pokemons) {
        Path path = Path.of("pokemon.json");

        try {
            // Anropar den privata metoden som sköter själva filskrivningen.
            writePokemonsToFile(pokemons, path);

            System.out.println("Du har sparat dina pokemons till fil.");

        } catch (PokemonSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    // Skriver Pokemon-listan till den angivna JSON-filen.
    private static void writePokemonsToFile(
            List<Pokemon> pokemons,
            Path path
    ) {
        // Använder ObjectMapper för att göra om Java-objekt till JSON.
        ObjectMapper objectMapper = new ObjectMapper();

        // Skriver först till en tillfällig fil.
        // På så sätt riskerar inte pokemon.json att bli halvskriven
        // om något går fel mitt under sparningen.
        Path tempPath = Path.of("pokemon.tmp");

        try {
            // Gör om Pokemon-listan till JSON och skriver den till fil.
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(tempPath.toFile(), pokemons);

            // När tempfilen har skrivits klart ersätter den gamla JSON-filen.
            Files.move(
                    tempPath,
                    path,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {
            // Fångar IOException och gör om felet till en egen exception.
            throw new PokemonSaveException(
                    "Kunde inte spara Pokemon-data.",
                    e
            );
        }
    }

    // Laddar sparad data om pokemon.json finns, annars laddas seed-filen.
    public static List<Pokemon> loadInitialPokemons(
            Path path,
            Path seedPath
    ) {
        try {
            if (Files.exists(path)) {
                return loadPokemonsFromFile(path);
            }

            // Om ingen sparad fil finns laddas startdata från seed-filen istället.
            List<Pokemon> pokemons = loadPokemonsFromFile(seedPath);

            System.out.println("Inga sparade pokemons finns. Startpokemon har laddats in.");
            return pokemons;

        } catch (PokemonLoadException e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    // Läser Pokemon-data från den angivna JSON-filen.
    public static List<Pokemon> loadPokemonsFromFile(Path path) {

        // Om filen inte finns returneras en tom lista.
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        // Använder ObjectMapper för att göra om JSON till Java-objekt.
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Läser JSON-filen och gör om innehållet till en array av Pokemon.
            Pokemon[] pokemonArray = objectMapper.readValue(
                    path.toFile(),
                    Pokemon[].class
            );

            // Gör om arrayen till en ArrayList.
            return new ArrayList<>(List.of(pokemonArray));

        } catch (IOException e) {
            // Fångar IOException och kastar en egen exception med ett tydligare felmeddelande.
            throw new PokemonLoadException(
                    "Kunde inte ladda Pokemon-data från " + path,
                    e
            );
        }
    }

    // Laddar Pokemon-data från fil om filen finns.
    public static List<Pokemon> loadPokemons(
            Path path,
            List<Pokemon> pokemons
    ) {
        // Behåller den nuvarande listan om ingen sparad fil finns.
        if (!Files.exists(path)) {
            System.out.println("Ingen sparad fil hittades.");
            return pokemons;
        }

        try {
            // Laddar in den sparade listan från filen.
            pokemons = loadPokemonsFromFile(path);
            System.out.println("Pokemon-data har laddats från fil.");
        } catch (PokemonLoadException e) {
            System.out.println(e.getMessage());
        }

        return pokemons;
    }

    // Återställer Pokemon-data från seed-filen efter bekräftelse.
    public static List<Pokemon> resetPokemons(
            Scanner scanner,
            List<Pokemon> pokemons,
            Path seedPath
    ) {
        // Frågar användaren innan den nuvarande datan ersätts med seed-datan.
        boolean confirm = InputHelper.readYesNo(
                scanner,
                "Är du säker på att du vill återställa all data? ja/nej: "
        );

        if (confirm) {
            try {
                // Laddar in originaldata från seed-filen.
                pokemons = loadPokemonsFromFile(seedPath);

                // Sparar seed-datan som den nya aktuella datan.
                savePokemonsToFile(pokemons);

                System.out.println("Data återställd till seedad data.");

            } catch (PokemonLoadException e) {
                System.out.println(e.getMessage());
            }
        }

        return pokemons;
    }
}

