package se.systementor.utbildning.service;

import se.systementor.utbildning.model.*;
import se.systementor.utbildning.ui.BattleMenu;
import se.systementor.utbildning.repository.BattleStatsFileService;
import se.systementor.utbildning.ui.Messages;

import se.systementor.utbildning.strategy.CpuStrategy;
import se.systementor.utbildning.strategy.RandomStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BattleService {
    private static final Random RANDOM = new Random();
    private static final BattleStats BATTLE_STATS = BattleStatsFileService.loadStats();

    // Använder CpuStrategy som datatyp så att vi enkelt
    // kan byta strategi utan att ändra stridslogiken.
    // RandomStrategy bestämmer hur CPU:n väljer handling.
    private static final CpuStrategy CPU_STRATEGY =
            new RandomStrategy();

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
            Attack playerAttack
    ) {
        playerAttack.execute(
                playerPokemon,
                wildPokemon
        );

        BATTLE_STATS.registerAttackUsage(playerAttack.getName());
    }


    private static void wildTurn(
            Pokemon wildPokemon,
            Pokemon playerPokemon
    ) {
        // Hämtar Pokemonens attacker och gör om dem till
        // BattleAction så att CPU:n kan välja via interfacet.
        List<BattleAction> actions =
                new ArrayList<>(wildPokemon.getAttacks());

        // Låter strategin bestämma vilken handling CPU:n ska använda.
        // BattleService behöver inte veta hur valet görs.
        BattleAction selectedAction =
                CPU_STRATEGY.chooseAction(actions);

        // Utför den valda handlingen via BattleAction-kontraktet.
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

                // För att spelaren ska kunna välja attack innan speed avgör vem som attackerar först.
                Attack playerAttack = BattleMenu.chooseAttack(
                        playerPokemon,
                        scanner
                );

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
                                playerAttack
                        );
                    } else {
                        wildTurn(
                                wildPokemon,
                                playerPokemon
                        );
                    }
                }

                if (battle.isOver()) {
                    Pokemon knockedOut = playerPokemon.isKnockedOut()
                            ? playerPokemon
                            : wildPokemon;

                    BattleMenu.showKnockedOut(knockedOut);
                }
            }

            BattleMenu.showBattleResult(
                    battle.getPlayerPokemon(),
                    battle.getWildPokemon()
            );

            BATTLE_STATS.registerPokemonUsage(playerPokemon.getId());

            if (battle.playerWon()) {
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

    // Väljer en slumpmässig attack från Pokemons attacker.
    public static Attack chooseRandomAttack(Pokemon pokemon) {
        List<Attack> attacks = pokemon.getAttacks();

        if (attacks.isEmpty()) {
            throw new IllegalArgumentException(
                    pokemon.getName() + " har inga attacker."
            );
        }

        return attacks.get(
                RANDOM.nextInt(attacks.size())
        );
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