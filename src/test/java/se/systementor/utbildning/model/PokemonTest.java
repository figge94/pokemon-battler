package se.systementor.utbildning.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import se.systementor.utbildning.exception.InvalidPokemonException;

import java.util.HashSet;
import java.util.Set;

class PokemonTest {

    @Test
    void hashSetShouldNotAllowDuplicatePokemonIds() {
        Set<Pokemon> pokemons = new HashSet<>();

        pokemons.add(new Pokemon(1, "Pikachu", Type.ELECTRIC, 100, 90));
        pokemons.add(new Pokemon(1, "Pikachu", Type.ELECTRIC, 100, 90));
        pokemons.add(new Pokemon(2, "Charmander", Type.FIRE, 100, 43));

        assertEquals(2, pokemons.size());
    }

    @Test
    void takeDamage_shouldReduceHp() {
        // Arrange - Förbered
        Pokemon pokemon = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 36
        );

        // Act - Utför
        pokemon.takeDamage(30);

        // Assert - Kontrollera
        assertEquals(70, pokemon.getCurrentHp());
    }

    @Test
    void restoreHealth_shouldRestoreFullHp() {
        Pokemon pokemon = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        pokemon.takeDamage(60);
        pokemon.restoreHealth();

        assertEquals(100, pokemon.getCurrentHp());
    }

    @Test
    void takeDamage_shouldNotAllowNegativeDamage() {
        Pokemon pokemon = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        assertThrows(IllegalArgumentException.class,
                () -> pokemon.takeDamage(-10));
    }

    @Test
    void heal_shouldRestoreHp() {
        Pokemon pokemon = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        pokemon.takeDamage(60);
        pokemon.heal(30);

        assertEquals(70, pokemon.getCurrentHp());
    }

    @Test
    void heal_shouldNotExceedMaxHp() {
        Pokemon pokemon = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        pokemon.takeDamage(20);
        pokemon.heal(50);

        assertEquals(100, pokemon.getCurrentHp());
    }

    @Test
    void isKnockedOut_shouldReturnTrueWhenHpIsZero() {
        Pokemon pokemon = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        pokemon.takeDamage(100);

        assertTrue(pokemon.isKnockedOut());
    }

    @Test
    void isKnockedOut_shouldReturnFalseWhenHpIsAboveZero() {
        Pokemon pokemon = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        pokemon.takeDamage(50);

        assertFalse(pokemon.isKnockedOut());
    }

    @Test
    void constructor_shouldRejectNegativeHp() {
        assertThrows(InvalidPokemonException.class,
                () -> new Pokemon(1, "Pikachu", Type.ELECTRIC, -10, 90));
    }

    @Test
    void constructor_shouldRejectEmptyName() {
        assertThrows(InvalidPokemonException.class,
                () -> new Pokemon(1, "", Type.ELECTRIC, 100, 90));
    }

    @Test
    void constructor_shouldRejectInvalidId() {
        assertThrows(InvalidPokemonException.class,
                () -> new Pokemon(0, "Pikachu", Type.ELECTRIC, 100, 90));
    }

    @Test
    void pokemonWithSameIdShouldBeEqual() {
        Pokemon pokemon1 = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        Pokemon pokemon2 = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        assertEquals(pokemon1, pokemon2);
        assertEquals(pokemon1.hashCode(), pokemon2.hashCode());
    }

    @Test
    void pokemonWithDifferentIdShouldNotBeEqual() {
        Pokemon pokemon1 = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        Pokemon pokemon2 = new Pokemon(
                2, "Pikachu", Type.ELECTRIC, 100, 90
        );

        assertNotEquals(pokemon1, pokemon2);
    }

    @Test
    void shouldNotReduceHpBelowZero() {
        Pokemon pokemon = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        // Skadan får inte sänka HP under noll.
        pokemon.takeDamage(150);

        assertEquals(0, pokemon.getCurrentHp());
        assertTrue(pokemon.isKnockedOut());
    }

    @Test
    void shouldRestoreHpToMaximum() {
        Pokemon pokemon = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        pokemon.takeDamage(60);

        // Kontrollerar att HP återställs helt.
        pokemon.restoreHealth();

        assertEquals(100, pokemon.getCurrentHp());
    }
}