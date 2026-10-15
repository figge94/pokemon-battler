package se.systementor.utbildning.strategy;

import se.systementor.utbildning.model.BattleAction;

import java.util.List;
import java.util.Random;

public class RandomStrategy implements CpuStrategy {

    private final Random random = new Random();

    @Override
    public BattleAction chooseAction(List<BattleAction> actions) {
        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException(
                    "CPU:n har inga handlingar att välja."
            );
        }

        return actions.get(random.nextInt(actions.size()));
    }
}
