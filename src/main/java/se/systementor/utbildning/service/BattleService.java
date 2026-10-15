package se.systementor.utbildning.service;

import se.systementor.utbildning.model.*;
import se.systementor.utbildning.ui.BattleMenu;
import se.systementor.utbildning.repository.BattleStatsFileService;
import se.systementor.utbildning.ui.Messages;
import se.systementor.utbildning.ui.BattlePresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BattleService {
    // Varje BattleService-objekt har sina egna beroenden.
    private final BattleStats battleStats;
    private final CpuPlayer cpuPlayer;

    public BattleService(BattleStats battleStats, CpuPlayer cpuPlayer) {
        this.battleStats = battleStats;
        this.cpuPlayer = cpuPlayer;
    }

    private static final Random RANDOM = new Random();

    // Väljer en slumpmässig vild Pokemon som inte är samma som spelarens Pokemon.
    public static Pokemon chooseRandomWildPokemon(
            List<Pokemon> pokemons,
            Pokemon playerPokemon
    ) {
        if (pokemons == null || playerPokemon == null) {
            throw new IllegalArgumentException(
                    "Pokemon-listan och spelarens Pokemon får inte vara null."
            );
        }

        if (pokemons.size() < 2) {
            throw new IllegalArgumentException(
                    "Det måste finnas minst två Pokemon för att starta en strid."
            );
        }

        boolean hasOpponent = pokemons.stream()
                .anyMatch(pokemon ->
                        pokemon != null && !pokemon.equals(playerPokemon)
                );

        if (!hasOpponent) {
            throw new IllegalArgumentException(
                    "Det finns ingen annan Pokemon att möta."
            );
        }

        Pokemon wildPokemon;

        do {
            int index = RANDOM.nextInt(pokemons.size());
            wildPokemon = pokemons.get(index);
        } while (wildPokemon == null || wildPokemon.equals(playerPokemon));

        return wildPokemon;
    }

    private static void executeAction(
            BattleAction action,
            Pokemon attacker,
            Pokemon defender
    ) {
        // Utför handlingen polymorft.
        ActionResult result = action.execute(attacker, defender);

        // Visar resultatet via den befintliga metoden.
        showActionResult(action, result, attacker, defender);
    }

    // Hanterar spelarens tur.
    private void playerTurn(
            Pokemon playerPokemon,
            Pokemon wildPokemon,
            BattleAction playerAction
    ) {
        // Utför spelarens handling via den gemensamma metoden.
        executeAction(playerAction, playerPokemon, wildPokemon);

        // Registrerar attacken i objektets statistik.
        battleStats.registerAttackUsage(playerAction.label());
    }

    private void wildTurn(
            Pokemon wildPokemon,
            Pokemon playerPokemon
    ) {
        // Hämtar CPU:ns tillgängliga handlingar.
        List<BattleAction> actions =
                new ArrayList<>(wildPokemon.getAttacks());

        // Använder CPU-spelaren som skickades in via konstruktorn.
        BattleAction selectedAction = cpuPlayer.chooseAction(actions);

        // Utför CPU:ns handling via den gemensamma metoden.
        executeAction(selectedAction, wildPokemon, playerPokemon);
    }

    // Startar och kör en hel strid tills någon Pokemon är besegrad.
    public void startBattle(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        Pokemon playerPokemon = null;
        Pokemon wildPokemon = null;

        try {
            playerPokemon = BattleMenu.choosePlayerPokemon(
                    pokemons, scanner
            );

            wildPokemon = chooseRandomWildPokemon(
                    pokemons, playerPokemon
            );

            // Kontrollerar att båda Pokemon har minst en attack.
            if (playerPokemon.getAttacks().isEmpty()
                    || wildPokemon.getAttacks().isEmpty()) {
                throw new IllegalArgumentException(
                        "Båda Pokemon måste ha minst en attack för att strida."
                );
            }

            Battle battle = new Battle(
                    playerPokemon,
                    wildPokemon
            );

            BattleMenu.showBattleStart(
                    battle.getPlayerPokemon(),
                    battle.getWildPokemon()
            );

            while (!battle.isOver()) {

                // Spelaren kan välja både attacker och andra handlingar.
                BattleAction playerAction = BattleMenu.chooseAction(
                        playerPokemon,
                        scanner
                );

                // Flykt sker innan turordningen bestäms.
                if (playerAction.endsBattle()) {
                    // Utför flykthandlingen och kontrollerar resultatet.
                    ActionResult result = playerAction.execute(
                            playerPokemon,
                            wildPokemon
                    );

                    if (result instanceof FleeResult) {
                        // Avslutar striden när flykten lyckas.
                        battle.flee();
                        break;
                    }

                    throw new IllegalStateException(
                            "Handlingen avslutar striden utan ett giltigt resultat."
                    );
                }

                Pokemon[] turnOrder = battle.getTurnOrder();

                BattlePresentation.showFirstAttacker(turnOrder[0]);

                for (Pokemon attacker : turnOrder) {

                    if (battle.isOver()) {
                        break;
                    }

                    if (attacker == playerPokemon) {
                        // Visar spelarens tur.
                        BattlePresentation.showTurn(playerPokemon, true);
                        BattlePresentation.pause();

                        playerTurn(playerPokemon, wildPokemon, playerAction);
                    } else {
                        // Visar CPU:ns tur.
                        BattlePresentation.showTurn(wildPokemon, false);
                        BattlePresentation.pause();

                        wildTurn(wildPokemon, playerPokemon);
                    }

                    BattleMenu.showBattleHp(playerPokemon, wildPokemon);
                    BattlePresentation.pause();

                }

                if (battle.isOver() && !battle.hasFled()) {
                    Pokemon knockedOut = playerPokemon.isKnockedOut()
                            ? playerPokemon
                            : wildPokemon;

                    BattleMenu.showKnockedOut(knockedOut);
                }
            }

            // Flykt avslutar striden utan vinnare.
            if (battle.hasFled()) {
                BattlePresentation.showFlee();
            } else {
                BattleMenu.showBattleResult(
                        battle.getPlayerPokemon(),
                        battle.getWildPokemon()
                );
            }

            // Registrerar och sparar stridens statistik.
            saveBattleStatistics(battle, playerPokemon);

        } catch (IllegalArgumentException e) {
            Messages.error(
                    "Ett fel uppstod under striden: " + e.getMessage()
            );
        } finally {
            // Återställer HP även om en strid avbryts av ett fel.
            if (playerPokemon != null) {
                playerPokemon.restoreHealth();
            }

            if (wildPokemon != null) {
                wildPokemon.restoreHealth();
            }
        }
    }

    // Hämtar statistik från det aktuella BattleService-objektet.
    public BattleStats getBattleStats() {
        return battleStats;
    }

    // Visar resultatet av en utförd handling.
    private static void showActionResult(
            BattleAction action,
            ActionResult result,
            Pokemon attacker,
            Pokemon defender
    ) {
        if (result instanceof AttackResult attackResult
                && action instanceof Attack attack) {
            BattleMenu.showAttackResult(
                    attacker,
                    defender,
                    attack,
                    attackResult
            );
        }
    }

    private void registerBattleResult(
            Battle battle,
            Pokemon playerPokemon
    ) {
        // Registrerar använd Pokémon och stridens resultat.
        battleStats.registerPokemonUsage(playerPokemon.getId());

        if (battle.hasFled()) {
            battleStats.addFlee();
        } else if (battle.playerWon()) {
            battleStats.addWin(playerPokemon.getId());
        } else {
            battleStats.addLoss(playerPokemon.getId());
        }
    }

    private void saveBattleStatistics(
            Battle battle,
            Pokemon playerPokemon
    ) {
        // Registrerar resultatet genom den gemensamma metoden.
        registerBattleResult(battle, playerPokemon);

        // Sparar statistiken till fil.
        // Sparar samma statistikobjekt som spelmenyn använder.
        BattleStatsFileService.saveStats(battleStats);
    }
}