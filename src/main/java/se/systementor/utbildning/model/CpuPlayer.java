
package se.systementor.utbildning.model;

import se.systementor.utbildning.strategy.CpuStrategy;

import java.util.List;

public class CpuPlayer {

    // Använder interface för att kunna byta strategi.
    private final CpuStrategy strategy;

    // Strategin skickas in via konstruktorn.
    public CpuPlayer(CpuStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "CPU-strategin får inte vara null."
            );
        }

        this.strategy = strategy;
    }

    // Låter strategin välja en handling åt CPU:n.
    public BattleAction chooseAction(List<BattleAction> actions) {
        return strategy.chooseAction(actions);
    }
}
