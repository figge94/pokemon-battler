package se.systementor.utbildning.service;

import org.junit.jupiter.api.Test;
import se.systementor.utbildning.exception.InvalidPokemonException;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PokemonServiceTest {

    @Test
    void createPokemonShouldNotReuseIdFromBattleStats() {
        BattleStats stats = new BattleStats();
        stats.addWin(10);

        Pokemon pokemon = PokemonService.createPokemon(
                new ArrayList<>(),
                "TestPokemon",
                100,
                Type.FIRE,
                50,
                stats
        );

        // ID 10 finns i historiken och får inte återanvändas.
        assertEquals(11, pokemon.getId());
    }

    @Test
    void createPokemonShouldStartWithIdOne() {
        BattleStats stats = new BattleStats();

        Pokemon pokemon = PokemonService.createPokemon(
                new ArrayList<>(),
                "TestPokemon",
                100,
                Type.FIRE,
                50,
                stats
        );

        // Första Pokémonen ska få ID 1.
        assertEquals(1, pokemon.getId());
    }

    @Test
    void createPokemonShouldUseNextAvailableId() {
        BattleStats stats = new BattleStats();

        Pokemon existingPokemon = PokemonService.createPokemon(
                new ArrayList<>(), "TestPokemon", 100, Type.FIRE, 50, stats
        );

        Pokemon newPokemon = PokemonService.createPokemon(
                new ArrayList<>(List.of(existingPokemon)),
                "EnAnnanPokemon",
                100,
                Type.WATER,
                60,
                stats
        );

        // Nästa Pokémon ska få ett högre ID än den befintliga.
        assertEquals(2, newPokemon.getId());
    }

    @Test
    void createPokemonShouldNotReuseIdFromLossStats() {
        BattleStats stats = new BattleStats();
        stats.addLoss(15);

        Pokemon pokemon = PokemonService.createPokemon(
                new ArrayList<>(),
                "TestPokemon",
                100,
                Type.FIRE,
                50,
                stats
        );

        // ID 15 finns i förlusthistoriken och får inte återanvändas.
        assertEquals(16, pokemon.getId());
    }

    @Test
    void createPokemonShouldNotReuseIdFromUsageStats() {
        BattleStats stats = new BattleStats();
        stats.registerPokemonUsage(20);

        Pokemon pokemon = PokemonService.createPokemon(
                new ArrayList<>(),
                "TestPokemon",
                100,
                Type.FIRE,
                50,
                stats
        );

        // ID 20 finns i historiken och får inte återanvändas.
        assertEquals(21, pokemon.getId());
    }

    @Test
    void editPokemonNameShouldThrowWhenPokemonIsMissing() {
        BattleStats stats = new BattleStats();

        Pokemon pokemon = PokemonService.createPokemon(
                new ArrayList<>(),
                "Pikachu",
                100,
                Type.ELECTRIC,
                50,
                stats
        );

        // Pokémonen finns inte i listan som ska redigeras.
        assertThrows(
                InvalidPokemonException.class,
                () -> PokemonService.editPokemonName(
                        new ArrayList<>(),
                        pokemon,
                        "Raichu"
                )
        );
    }

    @Test
    void editPokemonNameShouldReplacePokemonInList() {
        BattleStats stats = new BattleStats();

        Pokemon pokemon = PokemonService.createPokemon(
                new ArrayList<>(), "Pikachu", 100,
                Type.ELECTRIC, 50, stats
        );

        List<Pokemon> pokemons = new ArrayList<>();
        pokemons.add(pokemon);

        Pokemon updated = PokemonService.editPokemonName(
                pokemons, pokemon, "Raichu"
        );

        // Listan ska innehålla den uppdaterade Pokemonen.
        assertSame(updated, pokemons.get(0));
        assertEquals("Raichu", updated.getName());
    }

    @Test
    void editPokemonNameShouldPreserveListOrder() {
        BattleStats stats = new BattleStats();
        List<Pokemon> pokemons = new ArrayList<>();

        Pokemon first = PokemonService.createPokemon(
                pokemons, "Pikachu", 100, Type.ELECTRIC, 50, stats
        );
        pokemons.add(first);

        Pokemon second = PokemonService.createPokemon(
                pokemons, "Charmander", 100, Type.FIRE, 50, stats
        );
        pokemons.add(second);

        Pokemon updated = PokemonService.editPokemonName(
                pokemons, first, "Raichu"
        );

        // Namnändringen ska inte påverka ordningen.
        assertSame(updated, pokemons.get(0));
        assertSame(second, pokemons.get(1));
    }

    @Test
    void editPokemonTypeShouldReplacePokemonInList() {
        BattleStats stats = new BattleStats();
        List<Pokemon> pokemons = new ArrayList<>();

        Pokemon pokemon = PokemonService.createPokemon(
                pokemons, "Testmon", 100, Type.FIRE, 50, stats
        );
        pokemons.add(pokemon);

        Pokemon updated = PokemonService.editPokemonType(
                pokemons, pokemon, Type.WATER
        );

        // Den uppdaterade Pokemonen ska ligga kvar i listan.
        assertSame(updated, pokemons.get(0));
        assertEquals(Type.WATER, updated.getType());
    }

    @Test
    void editPokemonMaxHpShouldReplacePokemonInList() {
        BattleStats stats = new BattleStats();
        List<Pokemon> pokemons = new ArrayList<>();

        Pokemon pokemon = PokemonService.createPokemon(
                pokemons, "Testmon", 100, Type.FIRE, 50, stats
        );
        pokemons.add(pokemon);

        Pokemon updated = PokemonService.editPokemonMaxHp(
                pokemons, pokemon, 150
        );

        // Listan ska innehålla Pokémonen med uppdaterat max HP.
        assertSame(updated, pokemons.get(0));
        assertEquals(150, updated.getMaxHp());
    }

    @Test
    void editPokemonSpeedShouldReplacePokemonInList() {
        BattleStats stats = new BattleStats();
        List<Pokemon> pokemons = new ArrayList<>();

        Pokemon pokemon = PokemonService.createPokemon(
                pokemons, "Testmon", 100, Type.FIRE, 50, stats
        );
        pokemons.add(pokemon);

        Pokemon updated = PokemonService.editPokemonSpeed(
                pokemons, pokemon, 80
        );

        // Den uppdaterade Pokemon ska ligga kvar på samma plats.
        assertSame(updated, pokemons.get(0));
        assertEquals(80, updated.getSpeed());
    }

    @Test
    void createPokemonShouldRejectDuplicateName() {
        BattleStats stats = new BattleStats();
        List<Pokemon> pokemons = new ArrayList<>();

        Pokemon existing = PokemonService.createPokemon(
                pokemons, "Pikachu", 100, Type.ELECTRIC, 50, stats
        );
        pokemons.add(existing);

        // Namnkontrollen ska inte skilja på stora och små bokstäver.
        assertThrows(
                InvalidPokemonException.class,
                () -> PokemonService.createPokemon(
                        pokemons, "pikachu", 100, Type.ELECTRIC, 50, stats
                )
        );
    }
}
