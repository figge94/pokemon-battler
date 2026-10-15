package se.systementor.utbildning.service;

import se.systementor.utbildning.exception.InvalidPokemonException;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

import java.util.List;

public class PokemonService {

    // Statistik som behövs när nya Pokémon-ID:n skapas.
    private final BattleStats battleStats;

    // Får sitt beroende utifrån i stället för att hämta det själv.
    public PokemonService(BattleStats battleStats) {
        this.battleStats = battleStats;
    }

    // Söker efter en Pokémon utan att skilja på stora och små bokstäver.
    public Pokemon findPokemonByName(
            List<Pokemon> pokemons,
            String name
    ) {
        return pokemons.stream()
                .filter(pokemon -> pokemon.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // Hämtar nästa ID utan att återanvända ID från stridsstatistiken.
    private int getNextId(List<Pokemon> pokemons) {

        int maxId = pokemons.stream()
                .mapToInt(Pokemon::getId)
                .max()
                .orElse(0);

        // Hittar högsta ID från tidigare stridsstatistik.
        maxId = findMaxId(maxId, battleStats.getWinsByPokemon().keySet());
        maxId = findMaxId(maxId, battleStats.getLossesByPokemon().keySet());
        maxId = findMaxId(maxId, battleStats.getPokemonUsage().keySet());

        return maxId + 1;
    }

    private static int findMaxId(
            int currentMax,
            Iterable<Integer> ids
    ) {
        // Hittar det högsta ID:t utan att minska tidigare maxvärde.
        int maxId = currentMax;

        for (int id : ids) {
            maxId = Math.max(maxId, id);
        }

        return maxId;
    }

    // Kontrollerar om namnet används av någon annan Pokemon än den som redigeras.
    private static boolean isNameTakenByAnotherPokemon(
            List<Pokemon> pokemons,
            String name,
            int id
    ) {
        return pokemons.stream()
                .anyMatch(pokemon ->
                        pokemon.getId() != id
                                && pokemon.getName().equalsIgnoreCase(name)
                );
    }

    public Pokemon createPokemon(
            List<Pokemon> pokemons,
            String name,
            int hp,
            Type type,
            int speed
    ) {
        if (findPokemonByName(pokemons, name) != null) {
            throw new InvalidPokemonException(
                    name + " finns redan."
            );
        }

        // ID genereras med statistikobjektet som tillhör tjänsten.
        int id = getNextId(pokemons);

        Pokemon pokemon = new Pokemon(
                id,
                name,
                type,
                hp,
                speed
        );

        Attack attack = AttackService.createDefaultAttack(type);
        pokemon.addAttack(attack);

        return pokemon;
    }

    // Lägger till en ny Pokemon med validerad input.
    public void addPokemon(
            List<Pokemon> pokemons,
            Pokemon pokemon
    ) {
        pokemons.add(pokemon);
    }

    private static void replacePokemon(
            List<Pokemon> pokemons,
            Pokemon originalPokemon,
            Pokemon updatedPokemon
    ) {
        int index = pokemons.indexOf(originalPokemon);

        // Förhindrar att en Pokémon som saknas ersätts.
        if (index == -1) {
            throw new InvalidPokemonException(
                    "Pokemon hittades inte i listan."
            );
        }

        pokemons.set(index, updatedPokemon);
    }

    // Ändrar namnet på en Pokemon.
    public Pokemon editPokemonName(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            String newName
    ) {
        if (isNameTakenByAnotherPokemon(pokemons, newName, pokemon.getId())) {
            throw new InvalidPokemonException(
                    newName + " finns redan."
            );
        }

        Pokemon updatedPokemon = pokemon.withName(newName);

        replacePokemon(pokemons, pokemon, updatedPokemon);

        return updatedPokemon;
    }

    // Ändrar typ på en Pokemon.
    public Pokemon editPokemonType(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Type newType
    ) {
        Pokemon updatedPokemon = pokemon.withType(newType);

        replacePokemon(pokemons, pokemon, updatedPokemon);

        return updatedPokemon;
    }

    // Ändrar currentHp på en Pokemon.
    public void editPokemonCurrentHp(
            Pokemon pokemon,
            int hp
    ) {

        int difference = hp - pokemon.getCurrentHp();

        if (difference > 0) {
            pokemon.heal(difference);
        } else if (difference < 0) {
            pokemon.takeDamage(-difference);
        }
    }

    // Ändrar maxHp på en Pokemon.
    public Pokemon editPokemonMaxHp(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            int hp
    ) {
        Pokemon updatedPokemon = pokemon.withMaxHp(hp);

        replacePokemon(pokemons, pokemon, updatedPokemon);

        return updatedPokemon;
    }

    public Pokemon editPokemonSpeed(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            int speed
    ) {
        Pokemon updatedPokemon = pokemon.withSpeed(speed);

        replacePokemon(pokemons, pokemon, updatedPokemon);

        return updatedPokemon;
    }

    // Tar bort en Pokémon från listan.
    public void removePokemon(
            List<Pokemon> pokemons,
            Pokemon pokemon) {
        pokemons.remove(pokemon);
    }
}