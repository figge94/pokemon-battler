package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class BattleStats {

    private int wins;
    private int losses;

    private final Map<Integer, Integer> winsByPokemon;
    private final Map<Integer, Integer> lossesByPokemon;
    private final Map<Integer, Integer> pokemonUsage;

    // Används när ny statistik skapas.
    public BattleStats() {
        this(0, 0, null, null, null);
    }

    // Används när statistik läses från JSON.
    @JsonCreator
    public BattleStats(
            @JsonProperty("wins") int wins,
            @JsonProperty("losses") int losses,
            @JsonProperty("winsByPokemon")
            Map<Integer, Integer> winsByPokemon,
            @JsonProperty("lossesByPokemon")
            Map<Integer, Integer> lossesByPokemon,
            @JsonProperty("pokemonUsage")
            Map<Integer, Integer> pokemonUsage
    ) {
        if (wins < 0 || losses < 0) {
            throw new IllegalArgumentException(
                    "Statistik får inte vara negativ."
            );
        }

        this.wins = wins;
        this.losses = losses;

        this.winsByPokemon = winsByPokemon == null
                ? new HashMap<>()
                : new HashMap<>(winsByPokemon);

        this.lossesByPokemon = lossesByPokemon == null
                ? new HashMap<>()
                : new HashMap<>(lossesByPokemon);

        this.pokemonUsage = pokemonUsage == null
                ? new HashMap<>()
                : new HashMap<>(pokemonUsage);
    }

    // Registrerar en vinst för en Pokemon.
    public void addWin(int pokemonId) {
        wins++;
        winsByPokemon.merge(
                pokemonId,
                1,
                Integer::sum);
    }

    // Registrerar en förlust för en Pokemon.
    public void addLoss(int pokemonId) {
        losses++;
        lossesByPokemon.merge(
                pokemonId,
                1,
                Integer::sum
        );
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    @JsonIgnore
    public int getTotalBattles() {
        return wins + losses;
    }

    public Map<Integer, Integer> getWinsByPokemon() {
        return Map.copyOf(winsByPokemon);
    }

    public Map<Integer, Integer> getLossesByPokemon() {
        return Map.copyOf(lossesByPokemon);
    }

    // Räknar ut vinstprocenten för en Pokemon.
    @JsonIgnore
    public double getWinPercentage(int pokemonId) {
        int pokemonWins = winsByPokemon.getOrDefault(
                pokemonId,
                0
        );

        int pokemonLosses = lossesByPokemon.getOrDefault(
                pokemonId,
                0
        );

        int totalBattles = pokemonWins + pokemonLosses;

        if (totalBattles == 0) {
            return 0.0;
        }

        return pokemonWins * 100.0 / totalBattles;
    }

    @JsonIgnore
    public int getMostUsedPokemonId() {
        return pokemonUsage.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
    }

    // Registrerar att en Pokemon används i en strid.
    public void registerPokemonUsage(int pokemonId) {
        pokemonUsage.merge(
                pokemonId,
                1,
                Integer::sum);
    }

    public Map<Integer, Integer> getPokemonUsage() {
        return Map.copyOf(pokemonUsage);
    }
}