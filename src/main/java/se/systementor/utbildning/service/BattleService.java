package se.systementor.utbildning.service;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;
import se.systementor.utbildning.ui.BattleMenu;
import se.systementor.utbildning.model.Battle;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BattleService {
    private static final Random RANDOM = new Random();

    // Väljer en slumpmässig vild Pokemon som inte är samma som spelarens Pokemon.
    public static Pokemon chooseRandomWildPokemon(
            List<Pokemon> pokemons,
            Pokemon playerPokemon
    ) {
        // Kontrollerar att det finns minst två Pokemon innan en strid kan starta.
        if (pokemons.size() < 2) {
            throw new IllegalArgumentException(
                    "Det måste finnas minst två Pokemon för att starta en strid."
            );
        }

        Pokemon wildPokemon;

        do {
            int index = RANDOM.nextInt(pokemons.size());
            wildPokemon = pokemons.get(index);
        } while (wildPokemon == playerPokemon);

        return wildPokemon;
    }

    // Hanterar spelarens tur.
    private static void playerTurn(
            Pokemon playerPokemon,
            Pokemon wildPokemon,
            Scanner scanner
    ) {
        Attack playerAttack = BattleMenu.chooseAttack(playerPokemon, scanner);

        attack(
                playerPokemon,
                wildPokemon,
                playerAttack
        );
    }

    // Hanterar den vilda Pokemonens tur.
    private static void wildTurn(
            Pokemon wildPokemon,
            Pokemon playerPokemon
    ) {
        Attack wildAttack = chooseRandomAttack(wildPokemon);

        attack(
                wildPokemon,
                playerPokemon,
                wildAttack
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

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Striden kunde inte startas: " + e.getMessage()
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

    // Utför en attack och räknar ut träff, typeffektivitet och skada.
    public static void attack(
            Pokemon attacker,
            Pokemon defender,
            Attack attack
    ) {

        int roll = RANDOM.nextInt(100) + 1;

        if (roll > attack.getAccuracy()) {
            BattleMenu.showMiss(attacker, attack);
            return;
        }

        double effectiveness = getTypeEffectiveness(
                attack.getType(),
                defender.getType()
        );

        int damage = (int) (
                attack.getBaseDamage() * effectiveness
        );

        defender.takeDamage(damage);

        BattleMenu.showAttackResult(
                attacker,
                defender,
                attack,
                damage,
                effectiveness
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
}