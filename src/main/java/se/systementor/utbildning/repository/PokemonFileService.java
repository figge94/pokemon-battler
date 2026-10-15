package se.systementor.utbildning.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import se.systementor.utbildning.exception.PokemonLoadException;
import se.systementor.utbildning.exception.PokemonSaveException;
import se.systementor.utbildning.model.Pokemon;

import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PokemonFileService {
    private final Path path;
    private final ObjectMapper objectMapper;

    // Filsökvägen bestäms när filhanteraren skapas.
    public PokemonFileService(Path path) {
        this.path = path;

        // Samma ObjectMapper används för all JSON-hantering.
        this.objectMapper = new ObjectMapper();
    }

    // Kontrollerar om den sparade Pokémon-filen finns.
    public boolean saveFileExists() {
        return Files.exists(path);
    }

    // Skriver Pokemon-listan till den angivna JSON-filen.
    public void savePokemonsToFile(List<Pokemon> pokemons) {
        // Skapar tempfilen i samma mapp som den riktiga filen.
        Path tempPath = path.resolveSibling(
                path.getFileName().toString() + ".tmp"
        );

        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(tempPath.toFile(), pokemons);

            // När tempfilen har skrivits klart ersätter den gamla JSON-filen.
            Files.move(
                    tempPath,
                    path,
                    StandardCopyOption.REPLACE_EXISTING
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
    public List<Pokemon> loadInitialPokemons(Path seedPath) {

        if (Files.exists(path)) {
            try {
                return loadPokemonsFromFile(path);
            } catch (PokemonLoadException e) {
                System.out.println(
                        "Den sparade Pokemon-filen kunde inte läsas."
                );
                System.out.println(
                        "Startdata laddas i stället."
                );
            }
        }

        if (!Files.exists(seedPath)) {
            throw new PokemonLoadException(
                    "Seed-filen saknas: " + seedPath
            );
        }

        return loadPokemonsFromFile(seedPath);
    }

    // Läser Pokemon-data från den angivna JSON-filen.
    private List<Pokemon> loadPokemonsFromFile(Path path) {

        // Om filen inte finns returneras en tom lista.
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            // Läser JSON-filen och gör om innehållet till en array av Pokemon.
            Pokemon[] pokemonArray = objectMapper.readValue(
                    path.toFile(),
                    Pokemon[].class
            );

            return new ArrayList<>(List.of(pokemonArray));

        } catch (IOException e) {
            throw new PokemonLoadException(
                    "Kunde inte ladda Pokemon-data från " + path,
                    e
            );
        }
    }

    // Laddar Pokemon-data från fil om filen finns.
    public List<Pokemon> loadPokemons() {
        // Läser Pokemon från objektets filsökväg.
        return loadPokemonsFromFile(path);
    }

    // Återställer Pokemon-data från seed-filen efter bekräftelse.
    public List<Pokemon> resetPokemons(Path seedPath) {
        if (!Files.exists(seedPath)) {
            throw new PokemonLoadException(
                    "Seed-filen saknas: " + seedPath
            );
        }

        // Läser originaldata från seed-filen.
        List<Pokemon> pokemons = loadPokemonsFromFile(seedPath);

        // Sparar originaldata som aktuell Pokemon-lista.
        savePokemonsToFile(pokemons);

        return pokemons;
    }
}

