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
// Genom att samla filhanteringen här behöver övriga klasser
// inte känna till detaljerna kring Jackson eller filsystemet.
public class PokemonFileService {

    // Sökvägen till den aktuella Pokemon-filen.
    // ObjectMapper används för att omvandla mellan Java-objekt och JSON.
    private final Path path;
    private final ObjectMapper objectMapper;

    // Tar emot filsökvägen via konstruktorn.
    // Detta gör att filhanteraren kan användas med olika filer
    // utan att sökvägen behöver vara hårdkodad i klassen.
    public PokemonFileService(Path path) {
        this.path = path;

        // Skapar en ObjectMapper som återanvänds vid
        // både inläsning och sparande av Pokemon-data.
        this.objectMapper = new ObjectMapper();
    }

    // Kontrollerar om den sparade Pokemon-filen finns.
    // Används exempelvis för att avgöra om tidigare data kan laddas.
    public boolean saveFileExists() {
        return Files.exists(path);
    }

    // Sparar den aktuella Pokemon-listan till JSON-filen.
    // Skriver först till en temporär fil för att minska risken
    // att den befintliga sparfilen skadas vid ett skrivfel.
    public void savePokemonsToFile(List<Pokemon> pokemons) {

        // Skapar en tempfil i samma mapp som den riktiga JSON-filen.
        // Den får filändelsen .tmp och används tills skrivningen är klar.
        Path tempPath = path.resolveSibling(
                path.getFileName().toString() + ".tmp"
        );

        try {
            // Jackson omvandlar Pokemon-listan till JSON.
            // PrettyPrinter gör filens innehåll lättare att läsa.
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(tempPath.toFile(), pokemons);

            // Ersätter den gamla sparfilen först när tempfilen
            // har skrivits färdigt.
            Files.move(
                    tempPath,
                    path,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {
            // Om skrivning eller filflytt misslyckas skickas
            // ett eget undantag vidare till anropande klass.
            // UI kan sedan visa ett begripligt felmeddelande.
            throw new PokemonSaveException(
                    "Kunde inte spara Pokemon-data.",
                    e
            );
        }
    }

    // Laddar Pokemon-data när programmet startar.
    // Försöker först använda den sparade filen och
    // använder seed-filen om sparfilen saknas eller är ogiltig.
    public List<Pokemon> loadInitialPokemons(Path seedPath) {

        // Kontrollerar om det finns tidigare sparad Pokemon-data.
        if (Files.exists(path)) {
            try {
                // Försöker läsa den sparade JSON-filen.
                return loadPokemonsFromFile(path);

            } catch (PokemonLoadException e) {
                // Om sparfilen är trasig kan programmet
                // fortsätta genom att läsa in startdata i stället.
                System.out.println(
                        "Den sparade Pokemon-filen kunde inte läsas."
                );
                System.out.println(
                        "Startdata laddas i stället."
                );
            }
        }

        // Kontrollerar att seed-filen finns innan den läses.
        // Om både sparfilen och seed-filen är oanvändbara
        // skickas ett fel vidare till programmets uppstart.
        if (!Files.exists(seedPath)) {
            throw new PokemonLoadException(
                    "Seed-filen saknas: " + seedPath
            );
        }

        // Laddar startdata när det inte går att använda sparfilen.
        return loadPokemonsFromFile(seedPath);
    }

    // Läser Pokemon-data från en JSON-fil.
    // Metoden används vid uppstart, vanlig inläsning och återställning.
    private List<Pokemon> loadPokemonsFromFile(Path path) {

        // Returnerar en tom lista om filen inte finns.
        // På så sätt behöver metoden aldrig returnera null.
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            // Jackson läser JSON och skapar Pokemon-objekt.
            // Pokemon-konstruktorn validerar värdena vid skapandet.
            Pokemon[] pokemonArray = objectMapper.readValue(
                    path.toFile(),
                    Pokemon[].class
            );

            // Omvandlar arrayen till en ändringsbar lista.
            // Listan används sedan för att hantera Pokemon i menyerna.
            return new ArrayList<>(List.of(pokemonArray));

        } catch (IOException | IllegalArgumentException e) {

            // Hanterar både problem med JSON och ogiltiga värden.
            // Skickar vidare ett eget undantag som beskriver filfelet.
            throw new PokemonLoadException(
                    "Kunde inte ladda Pokemon-data från " + path,
                    e
            );
        }
    }

    // Läser in Pokemon-data från den ordinarie sparfilen.
    // Återanvänder den privata metoden för JSON-inläsning.
    public List<Pokemon> loadPokemons() {
        return loadPokemonsFromFile(path);
    }

    // Återställer Pokemon-data från seed-filen.
    // Anropande meny ansvarar för att användaren
    // har bekräftat återställningen innan metoden körs.
    public List<Pokemon> resetPokemons(Path seedPath) {

        // Kontrollerar att seed-filen finns innan återställningen.
        if (!Files.exists(seedPath)) {
            throw new PokemonLoadException(
                    "Seed-filen saknas: " + seedPath
            );
        }

        // Läser in originaldata från seed-filen.
        List<Pokemon> pokemons = loadPokemonsFromFile(seedPath);

        // Skriver originaldata till den vanliga sparfilen.
        // Därmed används den återställda datan även nästa gång programmet startas.
        savePokemonsToFile(pokemons);

        // Returnerar den återställda listan till huvudmenyn.
        return pokemons;
    }
}
