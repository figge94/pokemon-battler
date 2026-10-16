package se.systementor.utbildning.strategy;

import se.systementor.utbildning.model.BattleAction;

import java.util.List;
import java.util.Random;

// Slumpmässig CPU-strategi som implementerar CpuStrategy.
// CpuPlayer kan använda strategin utan att känna till hur valet görs.
public class RandomStrategy implements CpuStrategy {

    // final gör att referensen inte kan bytas ut.
    // Samma Random-objekt återanvänds vid varje val.
    private final Random random = new Random();

    // Väljer en slumpmässig handling från listan.
    @Override
    public BattleAction chooseAction(List<BattleAction> actions) {

        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException(
                    "CPU:n har inga handlingar att välja."
            );
        }

        // nextInt ger ett index mellan 0 och listans storlek minus 1.
        return actions.get(random.nextInt(actions.size()));
    }
}