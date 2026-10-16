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

// Ansvarar för att spara, läsa och återställa Pokemon-data via JSON.
public class PokemonFileService {
    private final Path path;

    // Jacksons ObjectMapper omvandlar mellan Java-objekt och JSON.
    private final ObjectMapper objectMapper;

    // Sökvägen skickas in via konstruktorn så att olika filer kan användas.
    public PokemonFileService(Path path) {
        this.path = path;
        this.objectMapper = new ObjectMapper();
    }

    // Kontrollerar om den ordinarie sparfilen finns.
    public boolean saveFileExists() {
        return Files.exists(path);
    }

    // Sparar först till en temporär fil för att skydda den befintliga sparfilen.
    public void savePokemonsToFile(List<Pokemon> pokemons) {

        // Tempfilen placeras i samma mapp och får ändelsen .tmp.
        Path tempPath = path.resolveSibling(
                path.getFileName().toString() + ".tmp"
        );

        try {
            // Jackson skriver JSON med indrag för bättre läsbarhet.
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(tempPath.toFile(), pokemons);

            // Ersätter sparfilen först när tempfilen har skrivits färdigt.
            Files.move(
                    tempPath,
                    path,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {
            throw new PokemonSaveException(
                    "Kunde inte spara Pokemon-data.",
                    e
            );
        }
    }

    // Läser startdata utan att ändra den ordinarie sparfilen.
    public List<Pokemon> loadSeedPokemons(Path seedPath) {

        if (!Files.exists(seedPath)) {
            throw new PokemonLoadException(
                    "Seed-filen saknas: " + seedPath
            );
        }

        return loadPokemonsFromFile(seedPath);
    }

    // Laddar sparad Pokemon-data eller startdata om sparfilen saknas.
    public List<Pokemon> loadInitialPokemons(Path seedPath) {

        if (Files.exists(path)) {
            try {
                return loadPokemonsFromFile(path);

            } catch (PokemonLoadException e) {
                // Skickar felet vidare så att Main kan hantera det.
                throw new PokemonLoadException(
                        "Den sparade Pokemon-filen kunde inte läsas.",
                        e
                );
            }
        }

        return loadSeedPokemons(seedPath);
    }

    // Läser JSON från valfri sökväg och returnerar en ändringsbar lista.
    private List<Pokemon> loadPokemonsFromFile(Path path) {

        // Returnerar en tom lista i stället för null om filen saknas.
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            // Jackson skapar Pokemon-objekt från JSON-filen.
            Pokemon[] pokemonArray = objectMapper.readValue(
                    path.toFile(),
                    Pokemon[].class
            );

            // List.of ger en oföränderlig lista, så vi skapar en ändringsbar kopia.
            return new ArrayList<>(List.of(pokemonArray));

        } catch (IOException | IllegalArgumentException e) {
            throw new PokemonLoadException(
                    "Kunde inte ladda Pokemon-data från " + path,
                    e
            );
        }
    }

    // Läser data från den ordinarie sparfilen.
    public List<Pokemon> loadPokemons() {
        return loadPokemonsFromFile(path);
    }

    // Återställer Pokemon från seed-filen och sparar resultatet.
    public List<Pokemon> resetPokemons(Path seedPath) {

        List<Pokemon> pokemons = loadSeedPokemons(seedPath);

        // Sparar återställningen så att den finns kvar efter omstart.
        savePokemonsToFile(pokemons);

        return pokemons;
    }

}
