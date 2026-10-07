package se.systementor.utbildning.service;

import se.systementor.utbildning.exception.InvalidPokemonException;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

import java.util.ArrayList;
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

    // Private eftersom metoden bara används i den här klassen.
    private static int getNextId(List<Pokemon> pokemons) {
        int maxId = 0;

        // Går igenom alla Pokemon för att hitta det högsta ID:t.
        for (Pokemon pokemon : pokemons) {
            if (pokemon.getId() > maxId) {
                maxId = pokemon.getId();
            }
        }

        // Returnerar nästa lediga ID genom att lägga till 1 på det högsta.
        return maxId + 1;
    }

    // Kontrollerar om ett Pokemon-namn redan finns i listan.
    private static boolean existsByName(List<Pokemon> pokemons, String name) {
        for (Pokemon pokemon : pokemons) {
            if (pokemon.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
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
            Type type
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
                hp
        );

        Attack attack = AttackService.createDefaultAttack(type);

        List<Attack> attacks = new ArrayList<>();
        attacks.add(attack);

        pokemon.changeAttacks(attacks);

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
        // Kontrollerar om namnet redan används av en annan Pokemon.
        if (existsByNameExceptId(pokemons, newName, pokemon.getId())) {
            throw new InvalidPokemonException(
                    newName + " finns redan."
            );
        }

        Pokemon updatedPokemon = new Pokemon(
                pokemon.getId(),
                newName,
                pokemon.getType(),
                pokemon.getMaxHp()
        );

        updatedPokemon.changeAttacks(
                new ArrayList<>(pokemon.getAttacks())
        );

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

        Pokemon updatedPokemon = new Pokemon(
                pokemon.getId(),
                pokemon.getName(),
                newType,
                pokemon.getMaxHp()
        );

        updatedPokemon.changeAttacks(
                new ArrayList<>(pokemon.getAttacks())
        );

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
        Pokemon updatedPokemon = new Pokemon(
                pokemon.getId(),
                pokemon.getName(),
                pokemon.getType(),
                hp
        );

        updatedPokemon.changeAttacks(
                new ArrayList<>(pokemon.getAttacks())
        );

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