package se.systementor.utbildning.model;

import se.systementor.utbildning.strategy.CpuStrategy;

import java.util.List;

// Använder Strategy Pattern för att låta olika strategier styra CPU:ns val.
public class CpuPlayer {

    // Interfacet CpuStrategy gör det möjligt att byta strategy utan att ändra CpuPlayer.
    private final CpuStrategy strategy;

    // Tar emot strategin via Dependency Injection.
    public CpuPlayer(CpuStrategy strategy) {

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "CPU-strategin får inte vara null."
            );
        }

        this.strategy = strategy;
    }

    // Låter den valda strategin bestämma vilken handling CPU:n ska utföra.
    public BattleAction chooseAction(List<BattleAction> actions) {
        return strategy.chooseAction(actions);
    }
}
