
package se.systementor.utbildning.strategy;

import se.systementor.utbildning.model.BattleAction;
import java.util.List;

// Definierar hur CPU:n ska välja en handling under striden.
// Olika strategier kan implementera interfacet utan att
// CpuPlayer eller stridslogiken behöver ändras.
public interface CpuStrategy {

    // Tar emot tillgängliga handlingar och returnerar den valda.
    // Varje implementation bestämmer själv hur valet görs.
    BattleAction chooseAction(List<BattleAction> actions);
}
