package se.systementor.utbildning.service;

import se.systementor.utbildning.model.*;
import se.systementor.utbildning.ui.BattleMenu;
import se.systementor.utbildning.repository.BattleStatsFileService;
import se.systementor.utbildning.ui.Messages;

import se.systementor.utbildning.strategy.RandomStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BattleService {
    private static final Random RANDOM = new Random();
    private static final BattleStats BATTLE_STATS = BattleStatsFileService.loadStats();

    // CPU:n får sin strategi via konstruktorn.
    private static final CpuPlayer CPU_PLAYER =
            new CpuPlayer(new RandomStrategy());

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
        // Utför handlingen via BattleAction-interfacet.
        playerAction.execute(
                playerPokemon,
                wildPokemon
        );

        // Registrerar användningen av handlingen.
        BATTLE_STATS.registerAttackUsage(playerAction.label());
    }


    private static void wildTurn(
            Pokemon wildPokemon,
            Pokemon playerPokemon
    ) {
        // Hämtar attackerna som BattleAction.
        List<BattleAction> actions =
                new ArrayList<>(wildPokemon.getAttacks());

        // CPU:n väljer handling via sin strategi.
        BattleAction selectedAction =
                CPU_PLAYER.chooseAction(actions);

        // Utför handlingen polymorft.
        selectedAction.execute(
                wildPokemon,
                playerPokemon
        );
    }


    // Startar och kör en hel strid tills någon Pokemon är besegrad.
    public static void startBattle(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        try {
            Pokemon playerPokemon = BattleMenu.choosePlayerPokemon(
                    pokemons,
                    scanner
            );

            Pokemon wildPokemon = chooseRandomWildPokemon(
                    pokemons,
                    playerPokemon
            );

            // Kontrollerar att båda Pokemon har minst en attack.
            if (playerPokemon.getAttacks().isEmpty()
                    || wildPokemon.getAttacks().isEmpty()) {
                throw new IllegalArgumentException(
                        "Båda Pokemon måste ha minst en attack för att strida."
                );
            }

            // Återställer båda Pokemons HP inför strid.
            playerPokemon.restoreHealth();
            wildPokemon.restoreHealth();

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
                    playerAction.execute(playerPokemon, wildPokemon);
                    battle.flee();
                    break;
                }

                Pokemon[] turnOrder = battle.getTurnOrder();

                System.out.println(
                        turnOrder[0].getName() + " är snabbast och attackerar först!"
                );

                for (Pokemon attacker : turnOrder) {

                    if (battle.isOver()) {
                        break;
                    }

                    if (attacker == playerPokemon) {

                        playerTurn(
                                playerPokemon,
                                wildPokemon,
                                playerAction
                        );

                    } else {
                        wildTurn(
                                wildPokemon,
                                playerPokemon
                        );
                    }
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
                System.out.println("Du flydde från striden!");
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


            // Återställer båda Pokemon efter avslutad strid.
            playerPokemon.restoreHealth();
            wildPokemon.restoreHealth();

            // Sparar statistiken efter varje avslutad strid.
            BattleStatsFileService.saveStats(BATTLE_STATS);

        } catch (IllegalArgumentException e) {
            Messages.error(
                    "Ett fel uppstod under striden: " + e.getMessage()
            );
        }
    }

    // Räknar ut hur effektiv attacktypen är mot försvararens typ.
    public static double getTypeEffectiveness(
            Type attackType,
            Type defenderType
    ) {
        return switch (attackType) {
            case FIRE -> switch (defenderType) {
                case GRASS -> 2.0;
                case WATER -> 0.5;
                default -> 1.0;
            };

            case WATER -> switch (defenderType) {
                case FIRE -> 2.0;
                case GRASS -> 0.5;
                default -> 1.0;
            };

            case GRASS -> switch (defenderType) {
                case WATER -> 2.0;
                case FIRE -> 0.5;
                default -> 1.0;
            };

            case ELECTRIC -> defenderType == Type.WATER ? 2.0 : 1.0;

            default -> 1.0;
        };
    }

    public static BattleStats getBattleStats() {
        return BATTLE_STATS;
    }
}