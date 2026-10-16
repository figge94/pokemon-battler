package se.systementor.utbildning.strategy;

import se.systementor.utbildning.model.BattleAction;

import java.util.List;

// Definierar hur CPU:n väljer handlingar (Strategy Pattern).
// Olika strategier kan användas utan att ändra CpuPlayer.
public interface CpuStrategy {

    // Varje strategi bestämmer själv hur en handling väljs.
    BattleAction chooseAction(List<BattleAction> actions);
}
