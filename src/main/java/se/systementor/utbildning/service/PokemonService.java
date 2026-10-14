package se.systementor.utbildning.service;

import se.systementor.utbildning.exception.InvalidPokemonException;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

import java.util.List;

public class PokemonService {

    // Letar efter en Pokemon med samma namn, oavsett stora eller små bokstäver.
    public static Pokemon findPokemonByName(List<Pokemon> pokemons, String name) {
        for (Pokemon pokemon : pokemons) {
            if (pokemon.getName().equalsIgnoreCase(name)) {
                return pokemon;
            }
        }

        return null;
    }

    // Hämtar nästa ID utan att återanvända ID från stridsstatistiken.
    private static int getNextId(List<Pokemon> pokemons) {
        int maxId = 0;

        // Kontrollerar alla Pokémon som finns kvar.
        for (Pokemon pokemon : pokemons) {
            maxId = Math.max(maxId, pokemon.getId());
        }

        BattleStats stats = BattleService.getBattleStats();

        // Kontrollerar Pokémon som tidigare vunnit strider.
        for (int id : stats.getWinsByPokemon().keySet()) {
            maxId = Math.max(maxId, id);
        }

        // Kontrollerar Pokémon som tidigare förlorat strider.
        for (int id : stats.getLossesByPokemon().keySet()) {
            maxId = Math.max(maxId, id);
        }

        // Kontrollerar alla Pokémon som tidigare använts.
        for (int id : stats.getPokemonUsage().keySet()) {
            maxId = Math.max(maxId, id);
        }

        return maxId + 1;
    }

    // Kontrollerar om ett Pokemon-namn redan finns i listan.
    private static boolean existsByName(
            List<Pokemon> pokemons,
            String name
    ) {
        return findPokemonByName(pokemons, name) != null;
    }

    // Kontrollerar om namnet används av någon annan Pokemon än den som redigeras.
    private static boolean existsByNameExceptId(
            List<Pokemon> pokemons,
            String name,
            int id
    ) {
        for (Pokemon pokemon : pokemons) {
            // Ignorerar den Pokemon som redigeras och kollar om någon annan har samma namn.
            if (pokemon.getId() != id &&
                    pokemon.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public static Pokemon createPokemon(
            List<Pokemon> pokemons,
            String name,
            int hp,
            Type type,
            int speed
    ) {
        int id = getNextId(pokemons);

        if (existsByName(pokemons, name)) {
            throw new InvalidPokemonException(
                    name + " finns redan."
            );
        }

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
    public static void addPokemon(
            List<Pokemon> pokemons,
            Pokemon pokemon
    ) {
        pokemons.add(pokemon);
    }

    // Ändrar namnet på en Pokemon.
    public static Pokemon editPokemonName(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            String newName
    ) {
        if (existsByNameExceptId(pokemons, newName, pokemon.getId())) {
            throw new InvalidPokemonException(
                    newName + " finns redan."
            );
        }

        Pokemon updatedPokemon = pokemon.withName(newName);

        int index = pokemons.indexOf(pokemon);
        pokemons.set(index, updatedPokemon);

        return updatedPokemon;
    }

    // Ändrar typ på en Pokemon.
    public static Pokemon editPokemonType(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Type newType
    ) {
        Pokemon updatedPokemon = pokemon.withType(newType);

        int index = pokemons.indexOf(pokemon);
        pokemons.set(index, updatedPokemon);

        return updatedPokemon;
    }

    // Ändrar currentHp på en Pokemon.
    public static void editPokemonCurrentHp(
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
    public static Pokemon editPokemonMaxHp(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            int hp
    ) {
        Pokemon updatedPokemon = pokemon.withMaxHp(hp);

        int index = pokemons.indexOf(pokemon);
        pokemons.set(index, updatedPokemon);

        return updatedPokemon;
    }

    public static Pokemon editPokemonSpeed(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            int speed
    ) {
        Pokemon updatedPokemon = pokemon.withSpeed(speed);

        int index = pokemons.indexOf(pokemon);
        pokemons.set(index, updatedPokemon);

        return updatedPokemon;
    }

    // Tar bort en Pokemon efter bekräftelse.
    public static void removePokemon(
            List<Pokemon> pokemons,
            Pokemon pokemon) {
        pokemons.remove(pokemon);
    }
}