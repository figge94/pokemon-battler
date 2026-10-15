package se.systementor.utbildning.service;

import org.junit.jupiter.api.Test;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
