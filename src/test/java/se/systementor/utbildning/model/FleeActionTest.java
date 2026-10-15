package se.systementor.utbildning.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FleeActionTest {

    @Test
    void shouldReturnFleeResult() {
        BattleAction action = new FleeAction();

        // Flykt ska returnera ett FleeResult.
        ActionResult result = action.execute(null, null);

        assertInstanceOf(FleeResult.class, result);
    }

    @Test
    void shouldEndBattle() {
        BattleAction action = new FleeAction();

        // Flykt ska avsluta striden.
        assertTrue(action.endsBattle());
    }
}
