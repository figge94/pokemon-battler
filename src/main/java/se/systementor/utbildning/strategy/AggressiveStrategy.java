package se.systementor.utbildning.strategy;

import se.systementor.utbildning.model.BattleAction;
import se.systementor.utbildning.model.DamageAttack;

import java.util.List;

// Implementerar en aggressiv strategi för CPU:n.
// Strategin prioriterar handlingar som orsakar skada.
// Genom CpuStrategy kan beteendet bytas utan att ändra CpuPlayer.
public class AggressiveStrategy implements CpuStrategy {

    // Implementerar metoden från CpuStrategy.
    // Tar emot CPU:ns tillgängliga handlingar och väljer en av dem.
    @Override
    public BattleAction chooseAction(List<BattleAction> actions) {

        // Kontrollerar att CPU:n har minst en tillgänglig handling.
        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException(
                    "CPU:n har inga handlingar att välja."
            );
        }

        // Söker efter den första handlingen som orsakar skada.
        // Om ingen DamageAttack finns används första handlingen i listan.
        return actions.stream()
                .filter(action -> action instanceof DamageAttack)
                .findFirst()
                .orElse(actions.get(0));
    }
}