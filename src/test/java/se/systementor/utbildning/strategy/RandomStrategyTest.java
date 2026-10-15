
package se.systementor.utbildning.strategy;

import org.junit.jupiter.api.Test;
import se.systementor.utbildning.model.BattleAction;
import se.systementor.utbildning.model.DamageAttack;
import se.systementor.utbildning.model.Type;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomStrategyTest {

    @Test
    void shouldChooseActionFromList() {

        BattleAction tackle = new DamageAttack(
                "Tackle", 40, 100, Type.NORMAL
        );

        BattleAction ember = new DamageAttack(
                "Ember", 40, 100, Type.FIRE
        );

        List<BattleAction> actions = List.of(tackle, ember);

        CpuStrategy strategy = new RandomStrategy();

        // Kontrollerar att CPU:n väljer en giltig handling.
        BattleAction chosen = strategy.chooseAction(actions);

        assertTrue(actions.contains(chosen));
    }
}
