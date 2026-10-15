package se.systementor.utbildning.model;

import se.systementor.utbildning.strategy.CpuStrategy;

import java.util.List;

// Representerar CPU-spelaren som väljer handlingar under striden.
// Använder Strategy Pattern för att skilja CPU:ns val av handling
// från själva stridslogiken.
public class CpuPlayer {

    // Lagrar den strategi som bestämmer hur CPU:n ska spela.
    // Genom att använda interfacet CpuStrategy kan olika
    // strategier användas utan att ändra CpuPlayer.
    private final CpuStrategy strategy;

    // Tar emot CPU-strategin via konstruktorn.
    // Detta är Dependency Injection eftersom strategin skapas
    // utanför klassen och skickas in när objektet skapas.
    public CpuPlayer(CpuStrategy strategy) {

        // Förhindrar att CPU-spelaren skapas utan en strategi.
        // Det säkerställer att det alltid finns ett sätt att välja handling.
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "CPU-strategin får inte vara null."
            );
        }

        // Sparar strategin så att den kan användas under striden.
        this.strategy = strategy;
    }

    // Låter den valda strategin bestämma vilken handling CPU:n ska utföra.
    public BattleAction chooseAction(List<BattleAction> actions) {

        // Delegerar beslutet till strategin i stället för att
        // själv innehålla olika villkor för attackval.
        return strategy.chooseAction(actions);
    }
}
