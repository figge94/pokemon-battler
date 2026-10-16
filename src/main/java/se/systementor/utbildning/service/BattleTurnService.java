package se.systementor.utbildning.service;

import se.systementor.utbildning.model.ActionResult;
import se.systementor.utbildning.model.BattleAction;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.CpuPlayer;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.ui.BattlePresentation;

import java.util.ArrayList;
import java.util.List;

// Hanterar spelarens och CPU:ns handlingar under striden.
public class BattleTurnService {

    // Dependency Injection: statistik och CPU-spelare
    // skickas in via konstruktorn.
    private final BattleStats battleStats;
    private final CpuPlayer cpuPlayer;

    public BattleTurnService(BattleStats battleStats, CpuPlayer cpuPlayer) {
        this.battleStats = battleStats;
        this.cpuPlayer = cpuPlayer;
    }

    // Polymorfism gör att rätt execute-metod anropas
    // oavsett vilken BattleAction som skickas in.
    private ActionResult executeAction(
            BattleAction action,
            Pokemon attacker,
            Pokemon defender
    ) {
        return action.execute(attacker, defender);
    }

    // Utför spelarens handling och registrerar använda attacker.
    public void playerTurn(
            Pokemon playerPokemon,
            Pokemon wildPokemon,
            BattleAction playerAction
    ) {
        ActionResult result = executeAction(
                playerAction,
                playerPokemon,
                wildPokemon
        );

        // Presentationsklassen ansvarar för utskriften av resultatet.
        BattlePresentation.showActionResult(
                playerAction,
                result,
                playerPokemon,
                wildPokemon
        );

        // Registrerar attacken så att användningsstatistik kan visas.
        battleStats.registerAttackUsage(playerAction.label());
    }

    // Utför CPU:ns handling med hjälp av dess strategi.
    public void wildTurn(
            Pokemon wildPokemon,
            Pokemon playerPokemon
    ) {
        // Attack-objekten kan behandlas som BattleAction (polymorfism).
        List<BattleAction> actions =
                new ArrayList<>(wildPokemon.getAttacks());

        // CpuPlayer låter sin CpuStrategy välja handling (Strategy Pattern).
        BattleAction selectedAction = cpuPlayer.chooseAction(actions);

        ActionResult result = executeAction(
                selectedAction,
                wildPokemon,
                playerPokemon
        );

        BattlePresentation.showActionResult(
                selectedAction,
                result,
                wildPokemon,
                playerPokemon
        );
    }
}