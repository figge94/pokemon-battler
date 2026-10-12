package se.systementor.utbildning.service;

import se.systementor.utbildning.model.*;
import se.systementor.utbildning.ui.BattleMenu;
import se.systementor.utbildning.repository.BattleStatsFileService;
import se.systementor.utbildning.ui.Messages;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BattleService {
    private static final Random RANDOM = new Random();
    private static final BattleStats BATTLE_STATS = BattleStatsFileService.loadStats();

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
            Scanner scanner
    ) {
        Attack playerAttack = BattleMenu.chooseAttack(playerPokemon, scanner);

        playerAttack.execute(
                playerPokemon,
                wildPokemon
        );
    }

    // Hanterar den vilda Pokemonens tur.
    private static void wildTurn(
            Pokemon wildPokemon,
            Pokemon playerPokemon
    ) {
        Attack wildAttack = chooseRandomAttack(wildPokemon);

        wildAttack.execute(
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

            // Återställer båda Pokemons HP inför striden.
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

                playerTurn(
                        battle.getPlayerPokemon(),
                        battle.getWildPokemon(),
                        scanner
                );

                if (battle.getWildPokemon().isKnockedOut()) {
                    BattleMenu.showKnockedOut(
                            battle.getWildPokemon()
                    );
                    break;
                }

                wildTurn(
                        battle.getWildPokemon(),
                        battle.getPlayerPokemon()
                );

                if (battle.getPlayerPokemon().isKnockedOut()) {
                    BattleMenu.showKnockedOut(
                            battle.getPlayerPokemon()
                    );
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
        if (attackType == Type.FIRE && defenderType == Type.GRASS) {
            return 2.0;
        }

        if (attackType == Type.WATER && defenderType == Type.FIRE) {
            return 2.0;
        }

        if (attackType == Type.GRASS && defenderType == Type.WATER) {
            return 2.0;
        }

        if (attackType == Type.ELECTRIC && defenderType == Type.WATER) {
            return 2.0;
        }

        if (attackType == Type.FIRE && defenderType == Type.WATER) {
            return 0.5;
        }

        if (attackType == Type.WATER && defenderType == Type.GRASS) {
            return 0.5;
        }

        if (attackType == Type.GRASS && defenderType == Type.FIRE) {
            return 0.5;
        }

        return 1.0;
    }

    public static BattleStats getBattleStats() {
        return BATTLE_STATS;
    }
}