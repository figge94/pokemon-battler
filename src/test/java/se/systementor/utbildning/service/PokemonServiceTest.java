package se.systementor.utbildning.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.systementor.utbildning.exception.InvalidPokemonException;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;
import se.systementor.utbildning.repository.PokemonFileService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PokemonServiceTest {

    private PokemonService pokemonService;
    private BattleStats stats;

    @BeforeEach
    void setUp() {
        // Varje test får egna objekt och påverkar inte andra tester.
        stats = new BattleStats();

        PokemonFileService fileService =
                new PokemonFileService(Path.of("pokemon-test.json"));

        pokemonService = new PokemonService(stats, fileService);
    }

    @Test
    void createPokemonShouldNotReuseIdFromBattleStats() {
        stats.addWin(10);

        Pokemon pokemon = pokemonService.createPokemon(
                new ArrayList<>(),
                "TestPokemon",
                100,
                Type.FIRE,
                50
        );

        // ID 10 finns i historiken och får inte återanvändas.
        assertEquals(11, pokemon.getId());
    }

    @Test
    void createPokemonShouldStartWithIdOne() {
        Pokemon pokemon = pokemonService.createPokemon(
                new ArrayList<>(),
                "TestPokemon",
                100,
                Type.FIRE,
                50
        );

        // Första Pokemon ska få ID 1.
        assertEquals(1, pokemon.getId());
    }

    @Test
    void createPokemonShouldUseNextAvailableId() {
        Pokemon existingPokemon = pokemonService.createPokemon(
                new ArrayList<>(),
                "TestPokemon",
                100,
                Type.FIRE,
                50
        );

        Pokemon newPokemon = pokemonService.createPokemon(
                new ArrayList<>(List.of(existingPokemon)),
                "EnAnnanPokemon",
                100,
                Type.WATER,
                60
        );

        // Nästa Pokemon ska få ett högre ID än den befintliga.
        assertEquals(2, newPokemon.getId());
    }

    @Test
    void createPokemonShouldNotReuseIdFromLossStats() {
        stats.addLoss(15);

        Pokemon pokemon = pokemonService.createPokemon(
                new ArrayList<>(),
                "TestPokemon",
                100,
                Type.FIRE,
                50
        );

        // ID 15 finns i förlusthistoriken och får inte återanvändas.
        assertEquals(16, pokemon.getId());
    }

    @Test
    void createPokemonShouldNotReuseIdFromUsageStats() {
        stats.registerPokemonUsage(20);

        Pokemon pokemon = pokemonService.createPokemon(
                new ArrayList<>(),
                "TestPokemon",
                100,
                Type.FIRE,
                50
        );

        // ID 20 finns i historiken och får inte återanvändas.
        assertEquals(21, pokemon.getId());
    }

    @Test
    void editPokemonNameShouldThrowWhenPokemonIsMissing() {
        Pokemon pokemon = pokemonService.createPokemon(
                new ArrayList<>(),
                "Pikachu",
                100,
                Type.ELECTRIC,
                50
        );

        // Pokemon finns inte i listan som ska redigeras.
        assertThrows(
                InvalidPokemonException.class,
                () -> pokemonService.editPokemonName(
                        new ArrayList<>(),
                        pokemon,
                        "Raichu"
                )
        );
    }

    @Test
    void editPokemonNameShouldReplacePokemonInList() {
        Pokemon pokemon = pokemonService.createPokemon(
                new ArrayList<>(),
                "Pikachu",
                100,
                Type.ELECTRIC,
                50
        );

        List<Pokemon> pokemons = new ArrayList<>();
        pokemons.add(pokemon);

        Pokemon updated = pokemonService.editPokemonName(
                pokemons,
                pokemon,
                "Raichu"
        );

        // Listan ska innehålla den uppdaterade Pokemon.
        assertSame(updated, pokemons.get(0));
        assertEquals("Raichu", updated.getName());
    }

    @Test
    void editPokemonNameShouldPreserveListOrder() {
        List<Pokemon> pokemons = new ArrayList<>();

        Pokemon first = pokemonService.createPokemon(
                pokemons,
                "Pikachu",
                100,
                Type.ELECTRIC,
                50
        );
        pokemons.add(first);

        Pokemon second = pokemonService.createPokemon(
                pokemons,
                "Charmander",
                100,
                Type.FIRE,
                50
        );
        pokemons.add(second);

        Pokemon updated = pokemonService.editPokemonName(
                pokemons,
                first,
                "Raichu"
        );

        // Namnändringen ska inte påverka ordningen.
        assertSame(updated, pokemons.get(0));
        assertSame(second, pokemons.get(1));
    }

    @Test
    void editPokemonTypeShouldReplacePokemonInList() {
        List<Pokemon> pokemons = new ArrayList<>();

        Pokemon pokemon = pokemonService.createPokemon(
                pokemons,
                "Testmon",
                100,
                Type.FIRE,
                50
        );
        pokemons.add(pokemon);

        Pokemon updated = pokemonService.editPokemonType(
                pokemons,
                pokemon,
                Type.WATER
        );

        // Den uppdaterade Pokemon ska ligga kvar i listan.
        assertSame(updated, pokemons.get(0));
        assertEquals(Type.WATER, updated.getType());
    }

    @Test
    void editPokemonMaxHpShouldReplacePokemonInList() {
        List<Pokemon> pokemons = new ArrayList<>();

        Pokemon pokemon = pokemonService.createPokemon(
                pokemons,
                "Testmon",
                100,
                Type.FIRE,
                50
        );
        pokemons.add(pokemon);

        Pokemon updated = pokemonService.editPokemonMaxHp(
                pokemons,
                pokemon,
                150
        );

        // Listan ska innehålla Pokemon med uppdaterat maxHP.
        assertSame(updated, pokemons.get(0));
        assertEquals(150, updated.getMaxHp());
    }

    @Test
    void editPokemonSpeedShouldReplacePokemonInList() {
        List<Pokemon> pokemons = new ArrayList<>();

        Pokemon pokemon = pokemonService.createPokemon(
                pokemons,
                "Testmon",
                100,
                Type.FIRE,
                50
        );
        pokemons.add(pokemon);

        Pokemon updated = pokemonService.editPokemonSpeed(
                pokemons,
                pokemon,
                80
        );

        // Den uppdaterade Pokemon ska ligga kvar på samma plats.
        assertSame(updated, pokemons.get(0));
        assertEquals(80, updated.getSpeed());
    }

    @Test
    void createPokemonShouldRejectDuplicateName() {
        List<Pokemon> pokemons = new ArrayList<>();

        Pokemon existing = pokemonService.createPokemon(
                pokemons,
                "Pikachu",
                100,
                Type.ELECTRIC,
                50
        );
        pokemons.add(existing);

        // Namnkontrollen ska inte skilja på stora och små bokstäver.
        assertThrows(
                InvalidPokemonException.class,
                () -> pokemonService.createPokemon(
                        pokemons,
                        "pikachu",
                        100,
                        Type.ELECTRIC,
                        50
                )
        );
    }
}
