package se.systementor.utbildning.service;

import se.systementor.utbildning.exception.InvalidPokemonException;
import se.systementor.utbildning.exception.PokemonSaveException;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;
import se.systementor.utbildning.repository.PokemonFileService;

import java.util.List;
import java.util.Optional;

public class PokemonService {

    // Statistik som behövs när nya Pokemon-ID:n skapas.
    private final BattleStats battleStats;
    private final PokemonFileService pokemonFileService;

    // Beroenden skickas in utifrån.
    public PokemonService(
            BattleStats battleStats,
            PokemonFileService pokemonFileService
    ) {
        this.battleStats = battleStats;
        this.pokemonFileService = pokemonFileService;
    }

    // Söker efter en Pokemon utan att skilja på stora och små bokstäver.
    // Optional visar att sökningen kan ge ett resultat eller vara tom.
    public Optional<Pokemon> findPokemonOptional(
            List<Pokemon> pokemons,
            String name
    ) {
        return pokemons.stream()
                .filter(pokemon ->
                        pokemon.getName().equalsIgnoreCase(name)
                )
                .findFirst();
    }

    // Söker efter en Pokemon via Optional-metoden.
    // Returnerar null tills övriga klasser har anpassats till Optional.
    public Pokemon findPokemonByName(
            List<Pokemon> pokemons,
            String name
    ) {
        return findPokemonOptional(pokemons, name)
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
        // Kontrollerar om namnet redan används.
        // ifPresent hanterar resultatet utan en separat null-kontroll.
        findPokemonOptional(pokemons, name)
                .ifPresent(existingPokemon -> {
                    throw new InvalidPokemonException(
                            name + " finns redan."
                    );
                });

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

        try {
            // Sparar den uppdaterade listan.
            pokemonFileService.savePokemonsToFile(pokemons);
        } catch (PokemonSaveException e) {
            // Ångrar tillägget om sparandet misslyckas.
            pokemons.remove(pokemon);
            throw e;
        }
    }

    public void addAttackToPokemon(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Attack attack
    ) {
        pokemon.addAttack(attack);

        try {
            pokemonFileService.savePokemonsToFile(pokemons);
        } catch (PokemonSaveException e) {
            // Ångra attacken om sparandet misslyckas.
            pokemon.removeAttack(attack);
            throw e;
        }
    }

    public void removeAttackFromPokemon(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Attack attack
    ) {
        // Spara den ursprungliga attacklistan.
        List<Attack> originalAttacks = pokemon.getAttacks();

        // Pokemon kontrollerar att attacken får tas bort.
        pokemon.removeAttack(attack);

        try {
            pokemonFileService.savePokemonsToFile(pokemons);
        } catch (PokemonSaveException e) {
            // Återställ attackerna och deras ursprungliga ordning.
            pokemon.replaceAttacks(originalAttacks);
            throw e;
        }
    }

    private static void replacePokemon(
            List<Pokemon> pokemons,
            Pokemon originalPokemon,
            Pokemon updatedPokemon
    ) {
        int index = pokemons.indexOf(originalPokemon);

        if (index == -1) {
            throw new InvalidPokemonException(
                    "Pokemon hittades inte i listan."
            );
        }

        pokemons.set(index, updatedPokemon);
    }

    private void saveOrRollback(
            List<Pokemon> pokemons,
            Pokemon originalPokemon,
            Pokemon updatedPokemon
    ) {
        try {
            pokemonFileService.savePokemonsToFile(pokemons);
        } catch (PokemonSaveException e) {
            // Återställ originalet om sparandet misslyckas.
            replacePokemon(pokemons, updatedPokemon, originalPokemon);
            throw e;
        }
    }

    // Ändrar namnet på en Pokemon.
    public Pokemon editPokemonName(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            String newName
    ) {
        if (isNameTakenByAnotherPokemon(
                pokemons, newName, pokemon.getId())) {
            throw new InvalidPokemonException(
                    newName + " finns redan."
            );
        }

        Pokemon updatedPokemon = pokemon.withName(newName);

        replacePokemon(pokemons, pokemon, updatedPokemon);

        saveOrRollback(pokemons, pokemon, updatedPokemon);

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

        saveOrRollback(pokemons, pokemon, updatedPokemon);

        return updatedPokemon;
    }

    // Ändrar currentHp på en Pokemon.
    public void editPokemonCurrentHp(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            int hp
    ) {
        int originalHp = pokemon.getCurrentHp();
        int difference = hp - originalHp;

        if (difference > 0) {
            pokemon.heal(difference);
        } else if (difference < 0) {
            pokemon.takeDamage(-difference);
        }

        try {
            pokemonFileService.savePokemonsToFile(pokemons);
        } catch (PokemonSaveException e) {
            // Återställ HP om sparandet misslyckas.
            int rollbackDifference = originalHp - pokemon.getCurrentHp();

            if (rollbackDifference > 0) {
                pokemon.heal(rollbackDifference);
            } else if (rollbackDifference < 0) {
                pokemon.takeDamage(-rollbackDifference);
            }

            throw e;
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

        saveOrRollback(pokemons, pokemon, updatedPokemon);

        return updatedPokemon;
    }

    public Pokemon editPokemonSpeed(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            int speed
    ) {
        Pokemon updatedPokemon = pokemon.withSpeed(speed);

        replacePokemon(pokemons, pokemon, updatedPokemon);

        saveOrRollback(pokemons, pokemon, updatedPokemon);

        return updatedPokemon;
    }

    // Tar bort en Pokemon från listan.
    public void removePokemon(
            List<Pokemon> pokemons,
            Pokemon pokemon
    ) {
        int index = pokemons.indexOf(pokemon);

        if (index == -1) {
            throw new InvalidPokemonException(
                    "Pokemon hittades inte i listan."
            );
        }

        pokemons.remove(index);

        try {
            // Sparar listan efter borttagningen.
            pokemonFileService.savePokemonsToFile(pokemons);
        } catch (PokemonSaveException e) {
            // Återställer Pokemon på samma plats om sparandet misslyckas.
            pokemons.add(index, pokemon);
            throw e;
        }
    }
}