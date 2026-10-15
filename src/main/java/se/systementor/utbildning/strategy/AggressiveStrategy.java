
package se.systementor.utbildning.strategy;

import se.systementor.utbildning.model.BattleAction;
import se.systementor.utbildning.model.DamageAttack;

import java.util.List;

public class AggressiveStrategy implements CpuStrategy {

    @Override
    public BattleAction chooseAction(List<BattleAction> actions) {

        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException(
                    "CPU:n har inga handlingar att välja."
            );
        }

        // Prioriterar handlingar som gör skada.
        for (BattleAction action : actions) {
            if (action instanceof DamageAttack) {
                return action;
            }
        }

        // Väljer första handlingen om ingen gör skada.
        return actions.get(0);
    }
}
