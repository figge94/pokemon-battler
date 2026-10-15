package se.systementor.utbildning.service;

import se.systementor.utbildning.model.*;
import se.systementor.utbildning.ui.BattleMenu;
import se.systementor.utbildning.repository.BattleStatsFileService;
import se.systementor.utbildning.ui.Messages;
import se.systementor.utbildning.ui.BattlePresentation;

import se.systementor.utbildning.strategy.AggressiveStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BattleService {
    private static final Random RANDOM = new Random();
    private static final BattleStats BATTLE_STATS = BattleStatsFileService.loadStats();

    // CPU:n får sin strategi via konstruktorn.
    // CPU:n använder en aggressiv strategi.
    private static final CpuPlayer CPU_PLAYER =
            new CpuPlayer(new AggressiveStrategy());

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

    // Hanterar spelarens tur.
    private static void playerTurn(
            Pokemon playerPokemon,
            Pokemon wildPokemon,
            BattleAction playerAction
    ) {
        // Utför handlingen polymorft.
        ActionResult result = playerAction.execute(
                playerPokemon,
                wildPokemon
        );

        // Visar resultatet genom vår gemensamma metod.
        showActionResult(
                playerAction,
                result,
                playerPokemon,
                wildPokemon
        );

        // Registrerar användningen av attacken.
        BATTLE_STATS.registerAttackUsage(playerAction.label());
    }

    private static void wildTurn(
            Pokemon wildPokemon,
            Pokemon playerPokemon
    ) {
        // Hämtar CPU:ns tillgängliga handlingar.
        List<BattleAction> actions =
                new ArrayList<>(wildPokemon.getAttacks());

        // CPU:n väljer handling med sin strategi.
        BattleAction selectedAction = CPU_PLAYER.chooseAction(actions);

        // Utför handlingen polymorft.
        ActionResult result = selectedAction.execute(
                wildPokemon,
                playerPokemon
        );

        // Visar CPU:ns resultat genom den gemensamma metoden.
        showActionResult(
                selectedAction,
                result,
                wildPokemon,
                playerPokemon
        );
    }

    // Startar och kör en hel strid tills någon Pokemon är besegrad.
    public static void startBattle(
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
                        battle.flee();
                    }

                    break;
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

            BATTLE_STATS.registerPokemonUsage(playerPokemon.getId());

            // Registrerar vinster och förluster men inte flykter.
            if (battle.hasFled()) {
                // Registrerar flykten separat.
                BATTLE_STATS.addFlee();
            } else if (battle.playerWon()) {
                BATTLE_STATS.addWin(playerPokemon.getId());
            } else {
                BATTLE_STATS.addLoss(playerPokemon.getId());
            }

            // Sparar statistiken efter varje avslutad strid.
            BattleStatsFileService.saveStats(BATTLE_STATS);

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

    public static BattleStats getBattleStats() {
        return BATTLE_STATS;
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
}