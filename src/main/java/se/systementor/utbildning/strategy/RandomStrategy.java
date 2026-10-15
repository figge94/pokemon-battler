package se.systementor.utbildning.strategy;

import se.systementor.utbildning.model.BattleAction;

import java.util.List;
import java.util.Random;

// Implementerar en strategi där CPU:n väljer en slumpmässig handling.
// Genom att implementera CpuStrategy kan klassen användas av CpuPlayer
// utan att stridslogiken behöver känna till hur valet görs.
public class RandomStrategy implements CpuStrategy {

    // Slumpgenerator som används för att välja en handling.
    // final innebär att referensen inte kan bytas ut efter skapandet.
    // Samma Random-objekt återanvänds vid varje attackval.
    private final Random random = new Random();

    // Implementerar metoden chooseAction från CpuStrategy.
    // Tar emot CPU:ns tillgängliga handlingar och returnerar en av dem.
    @Override
    public BattleAction chooseAction(List<BattleAction> actions) {

        // Kontrollerar att listan finns och innehåller minst en handling.
        // Förhindrar fel när CPU:n försöker välja från en tom lista.
        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException(
                    "CPU:n har inga handlingar att välja."
            );
        }

        // Slumpar ett index mellan 0 och listans storlek minus 1.
        // Hämtar handlingen på det indexet och returnerar den.
        return actions.get(random.nextInt(actions.size()));
    }
}