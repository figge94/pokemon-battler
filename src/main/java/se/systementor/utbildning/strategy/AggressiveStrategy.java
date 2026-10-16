package se.systementor.utbildning.strategy;

import se.systementor.utbildning.model.BattleAction;
import se.systementor.utbildning.model.DamageAttack;

import java.util.List;

// Aggressiv CPU-strategi som prioriterar skadeattacker.
// CpuStrategy gör att strategin kan bytas utan att ändra CpuPlayer.
public class AggressiveStrategy implements CpuStrategy {

    // Väljer en skadeattack om en sådan finns.
    @Override
    public BattleAction chooseAction(List<BattleAction> actions) {

        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException(
                    "CPU:n har inga handlingar att välja."
            );
        }

        // Stream filtrerar fram skadeattacker med instanceof.
        // findFirst hämtar den första som en Optional.
        // orElse använder första handlingen om ingen skadeattack finns.
        return actions.stream()
                .filter(action -> action instanceof DamageAttack)
                .findFirst()
                .orElse(actions.get(0));
    }
}