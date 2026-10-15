package se.systementor.utbildning.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BattleTest {

    @Test
    void shouldEndBattleWhenPlayerFlees() {
        Pokemon player = new Pokemon(
                1, "Pikachu", Type.ELECTRIC, 100, 90
        );

        Pokemon wild = new Pokemon(
                2, "Horsea", Type.WATER, 100, 60
        );

        Battle battle = new Battle(player, wild);

        // En ny strid ska vara aktiv.
        assertFalse(battle.isOver());

        battle.flee();

        // Flykt ska avsluta striden utan vinnare.
        assertTrue(battle.hasFled());
        assertTrue(battle.isOver());
        assertFalse(battle.playerWon());
    }
}
