package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

// Samlar statistik från strider och kan spara och läsa in den med Jackson.
public class BattleStats {

    // Totalt antal vinster, förluster och flykter.
    private int wins;
    private int losses;
    private int flees;

    // Map kopplar Pokemons ID och attacknamn till statistik.
    private final Map<Integer, Integer> winsByPokemon;
    private final Map<Integer, Integer> lossesByPokemon;
    private final Map<Integer, Integer> pokemonUsage;
    private final Map<String, Integer> attackUsage;

    // Skapar tom statistik genom att anropa den andra konstruktorn.
    public BattleStats() {
        this(
                0,
                0,
                0,
                null,
                null,
                null,
                null
        );
    }

    // @JsonCreator låter Jackson skapa objektet från JSON.
    // @JsonProperty kopplar JSON-fälten till konstruktorns parametrar.
    @JsonCreator
    public BattleStats(
            @JsonProperty("wins") int wins,
            @JsonProperty("losses") int losses,
            @JsonProperty("flees") int flees,
            @JsonProperty("winsByPokemon") Map<Integer, Integer> winsByPokemon,
            @JsonProperty("lossesByPokemon") Map<Integer, Integer> lossesByPokemon,
            @JsonProperty("pokemonUsage") Map<Integer, Integer> pokemonUsage,
            @JsonProperty("attackUsage") Map<String, Integer> attackUsage
    ) {
        // Förhindrar att statistik skapas med negativa totalvärden.
        if (wins < 0 || losses < 0 || flees < 0) {
            throw new IllegalArgumentException(
                    "Statistik får inte vara negativ."
            );
        }

        // Sparar de totala resultaten i objektets privata fält.
        this.wins = wins;
        this.losses = losses;
        this.flees = flees;

        // Skapar egna Map-objekt för att skydda den interna statistiken.
        // Om ett värde saknas används en tom HashMap.
        this.winsByPokemon = winsByPokemon == null
                ? new HashMap<>()
                : new HashMap<>(winsByPokemon);

        this.lossesByPokemon = lossesByPokemon == null
                ? new HashMap<>()
                : new HashMap<>(lossesByPokemon);

        this.pokemonUsage = pokemonUsage == null
                ? new HashMap<>()
                : new HashMap<>(pokemonUsage);

        this.attackUsage = attackUsage == null
                ? new HashMap<>()
                : new HashMap<>(attackUsage);
    }

    // Registrerar en vinst både totalt och för den aktuella Pokemon.
    public void addWin(int pokemonId) {
        wins++;

        // merge skapar värdet 1 om ID:t saknas,
        // annars ökas det befintliga värdet med 1.
        winsByPokemon.merge(
                pokemonId,
                1,
                Integer::sum
        );
    }

    // Registrerar en förlust både totalt och för den aktuella Pokemon.
    public void addLoss(int pokemonId) {
        losses++;

        lossesByPokemon.merge(
                pokemonId,
                1,
                Integer::sum
        );
    }

    // Returnerar det totala antalet vinster.
    public int getWins() {
        return wins;
    }

    // Returnerar det totala antalet förluster.
    public int getLosses() {
        return losses;
    }

    // Beräknar totalt antal strider. @JsonIgnore förhindrar separat lagring i JSON.
    @JsonIgnore
    public int getTotalBattles() {
        return wins + losses + flees;
    }

    // Returnerar en oföränderlig kopia av vinster per Pokemon.
    public Map<Integer, Integer> getWinsByPokemon() {
        return Map.copyOf(winsByPokemon);
    }

    // Returnerar en skrivskyddad kopia av förluster per Pokemon.
    public Map<Integer, Integer> getLossesByPokemon() {
        return Map.copyOf(lossesByPokemon);
    }

    // Beräknar vinstprocenten för en Pokemon, utan att räkna med flykter.
    @JsonIgnore
    public double getWinPercentage(int pokemonId) {

        // Hämtar antalet vinster och förluster för Pokemon.
        // getOrDefault returnerar noll om ID:t inte finns i Map.
        int pokemonWins = winsByPokemon.getOrDefault(
                pokemonId,
                0
        );

        int pokemonLosses = lossesByPokemon.getOrDefault(
                pokemonId,
                0
        );

        // Räknar ut antalet strider med vinst eller förlust.
        int totalBattles = pokemonWins + pokemonLosses;

        // Förhindrar division med noll om Pokemon saknar resultat.
        if (totalBattles == 0) {
            return 0.0;
        }

        // Omvandlar antalet vinster till en procentandel.
        return pokemonWins * 100.0 / totalBattles;
    }

    // Beräknar förhållandet mellan vinster och förluster.
    @JsonIgnore
    public double getWinLossRatio(int pokemonId) {

        // Hämtar statistik utan att behöva kontrollera om nyckeln finns.
        int wins = winsByPokemon.getOrDefault(pokemonId, 0);
        int losses = lossesByPokemon.getOrDefault(pokemonId, 0);

        // Hanterar fall där Pokemon inte har några förluster.
        // Ingen statistik ger 0 och vinster utan förluster ger oändlighet.
        if (losses == 0) {
            return wins == 0 ? 0.0 : Double.POSITIVE_INFINITY;
        }

        // Returnerar antalet vinster dividerat med antalet förluster.
        return (double) wins / losses;
    }

    // Hittar ID:t för den mest använda Pokemon.
    @JsonIgnore
    public int getMostUsedPokemonId() {

        // Stream hittar det högsta användningsvärdet och hämtar dess ID.
        // Optional returnerar -1 om ingen Pokemon har använts.
        return pokemonUsage.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
    }

    // Returnerar en skrivskyddad kopia av attackstatistiken.
    public Map<String, Integer> getAttackUsage() {
        return Map.copyOf(attackUsage);
    }

    // Registrerar hur många gånger en Pokemon har använts i strid.
    public void registerPokemonUsage(int pokemonId) {

        pokemonUsage.merge(
                pokemonId,
                1,
                Integer::sum
        );
    }

    // Registrerar varje gång en viss attack används.
    public void registerAttackUsage(String attackName) {

        // Attacknamnet används som nyckel och antalet som värde.
        attackUsage.merge(
                attackName,
                1,
                Integer::sum
        );
    }

    // Returnerar en skrivskyddad kopia av Pokemon-användningen.
    public Map<Integer, Integer> getPokemonUsage() {
        return Map.copyOf(pokemonUsage);
    }

    // Returnerar antalet gånger spelaren har flytt från en strid.
    public int getFlees() {
        return flees;
    }

    // Registrerar att spelaren har flytt från en strid.
    public void addFlee() {
        flees++;
    }
}