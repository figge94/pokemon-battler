
package se.systementor.utbildning.strategy;

import se.systementor.utbildning.model.BattleAction;
import java.util.List;

// Ett interface som bestämmer hur CPU:n ska välja
// en handling under striden.
//
// Genom att använda ett interface kan vi skapa olika
// strategier utan att ändra själva stridslogiken.
public interface CpuStrategy {

    // Tar emot CPU:ns tillgängliga handlingar och
    // returnerar den handling som strategin väljer.
    //
    // Hur valet görs bestäms av klassen som
    // implementerar detta interface.
    BattleAction chooseAction(List<BattleAction> actions);
}
