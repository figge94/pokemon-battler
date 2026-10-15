package se.systementor.utbildning.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import se.systementor.utbildning.exception.PokemonLoadException;
import se.systementor.utbildning.exception.PokemonSaveException;
import se.systementor.utbildning.model.Pokemon;

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

    // Sparar listan med Pokemon till standardfilen pokemon.json.
    public void savePokemonsToFile(List<Pokemon> pokemons) {
        // Repository ansvarar för filskrivningen.
        writePokemonsToFile(pokemons, path);
    }

    // Skriver Pokemon-listan till den angivna JSON-filen.
    private void writePokemonsToFile(
            List<Pokemon> pokemons,
            Path path
    ) {

        // Skapar tempfilen i samma mapp som den riktiga filen.
        Path tempPath = path.resolveSibling(
                path.getFileName().toString() + ".tmp"
        );

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
    public List<Pokemon> loadInitialPokemons(Path seedPath) {
        // Använder sparad data om den finns.
        if (Files.exists(path)) {
            return loadPokemonsFromFile(path);
        }

        // Använder startdata när ingen sparad fil finns.
        return loadPokemonsFromFile(seedPath);
    }


    // Läser Pokemon-data från den angivna JSON-filen.
    public List<Pokemon> loadPokemonsFromFile(Path path) {

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
    public List<Pokemon> loadPokemons() {
        // Läser Pokémon från objektets filsökväg.
        return loadPokemonsFromFile(path);
    }

    // Återställer Pokemon-data från seed-filen efter bekräftelse.
    public List<Pokemon> resetPokemons(Path seedPath) {
        // Läser originaldata från seed-filen.
        List<Pokemon> pokemons = loadPokemonsFromFile(seedPath);

        // Sparar originaldata som aktuell Pokemon-lista.
        savePokemonsToFile(pokemons);

        return pokemons;
    }
}

