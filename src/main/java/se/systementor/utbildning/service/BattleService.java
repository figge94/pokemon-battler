package se.systementor.utbildning.service;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.ui.BattleMenu;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BattleService {

    // Väljer en slumpmässig Pokemon som motståndare.
    public static Pokemon chooseRandomWildPokemon(List<Pokemon> pokemons) {
        Random random = new Random();

        int index = random.nextInt(pokemons.size());

        return pokemons.get(index);
    }

    public static void startBattle(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        Pokemon playerPokemon = BattleMenu.choosePlayerPokemon(pokemons, scanner);
        Pokemon wildPokemon = chooseRandomWildPokemon(pokemons);

        BattleMenu.showBattleStart(
                playerPokemon,
                wildPokemon
        );

        while (!playerPokemon.isKnockedOut() && !wildPokemon.isKnockedOut()) {

            Attack playerAttack = BattleMenu.chooseAttack(playerPokemon, scanner);

            attack(
                    playerPokemon,
                    wildPokemon,
                    playerAttack
            );

            if (wildPokemon.isKnockedOut()) {
                BattleMenu.showKnockedOut(wildPokemon);
                break;
            }

            List<Attack> wildAttacks = wildPokemon.getAttacks();

            Attack wildAttack = wildAttacks.get(
                    new Random().nextInt(wildAttacks.size())
            );

            attack(
                    wildPokemon,
                    playerPokemon,
                    wildAttack
            );

            if (playerPokemon.isKnockedOut()) {
                BattleMenu.showKnockedOut(playerPokemon);
            }
        }
    }

    public static void attack(
            Pokemon attacker,
            Pokemon defender,
            Attack attack
    ) {
        Random random = new Random();

        int roll = random.nextInt(100) + 1;

        if (roll > attack.getAccuracy()) {
            BattleMenu.showMiss(attacker, attack);
            return;
        }

        int damage = attack.getBaseDamage();
        defender.takeDamage(damage);

        BattleMenu.showAttackResult(
                attacker,
                defender,
                attack,
                damage
        );
    }
}