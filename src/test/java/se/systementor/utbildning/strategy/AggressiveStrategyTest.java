
package se.systementor.utbildning.strategy;

import org.junit.jupiter.api.Test;
import se.systementor.utbildning.model.BattleAction;
import se.systementor.utbildning.model.DamageAttack;
import se.systementor.utbildning.model.FleeAction;
import se.systementor.utbildning.model.Type;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AggressiveStrategyTest {

    @Test
    void shouldChooseDamageAttackInsteadOfFlee() {

        // Skapar två olika handlingar.
        BattleAction flee = new FleeAction();
        BattleAction attack = new DamageAttack(
                "Tackle", 40, 100, Type.NORMAL
        );

        // Lägger Fly först för att testa prioriteringen.
        List<BattleAction> actions = List.of(flee, attack);

        CpuStrategy strategy = new AggressiveStrategy();

        // Kontrollerar att CPU:n väljer skadeattacken.
        BattleAction chosen = strategy.chooseAction(actions);

        assertSame(attack, chosen);
    }
}
