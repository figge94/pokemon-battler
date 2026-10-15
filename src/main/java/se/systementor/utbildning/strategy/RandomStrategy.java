
package se.systementor.utbildning.strategy;

import se.systementor.utbildning.model.BattleAction;

import java.util.List;
import java.util.Random;

// Den här klassen använder CpuStrategy-kontraktet
// för att välja en slumpmässig handling åt CPU:n.
public class RandomStrategy implements CpuStrategy {

    private final Random random = new Random();

    // Override används eftersom vi implementerar
    // metoden som finns i CpuStrategy-interfacet.
    @Override
    public BattleAction chooseAction(List<BattleAction> actions) {

        // Kontrollerar att det finns handlingar att välja.
        // Annars skulle programmet kunna krascha vid slumpvalet.
        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException(
                    "CPU:n har inga handlingar att välja mellan."
            );
        }

        // Slumpar ett index inom listans storlek och
        // returnerar handlingen på den positionen.
        return actions.get(random.nextInt(actions.size()));
    }
}
