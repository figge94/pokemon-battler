package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.Pokemon;

import java.util.List;
import java.util.Scanner;

public class BattleMenu {

    // Visar attacker spelaren kan välja mellan i en strid
    public static Attack chooseAttack(
            Pokemon pokemon,
            Scanner scanner
    ) {
        List<Attack> attacks = pokemon.getAttacks();

        if (attacks.isEmpty()) {
            throw new IllegalArgumentException(
                    pokemon.getName() + " har inga attacker."
            );
        }

        System.out.println("\n=== VÄLJ ATTACK ===");

        for (int i = 0; i < attacks.size(); i++) {
            Attack attack = attacks.get(i);

            System.out.println(
                    (i + 1)
                            + ". "
                            + attack.getName()
                            + " | Skada: "
                            + attack.getBaseDamage()
            );
        }

        int choice = InputHelper.readIntInRange(
                scanner,
                "Välj attack: ",
                1,
                attacks.size()
        );

        return attacks.get(choice - 1);
    }

    // Låter spelaren välja vilken Pokemon som ska användas i striden.
    public static Pokemon choosePlayerPokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        while (true) {
            System.out.println("\n=== VÄLJ POKEMON ===");

            for (int i = 0; i < pokemons.size(); i++) {
                Pokemon pokemon = pokemons.get(i);

                System.out.println(
                        (i + 1) + ". "
                                + pokemon.getName()
                                + " | HP: "
                                + pokemon.getCurrentHp()
                                + "/"
                                + pokemon.getMaxHp()
                );
            }

            int choice = InputHelper.readIntInRange(
                    scanner,
                    "Välj Pokemon: ",
                    1,
                    pokemons.size()
            );

            Pokemon chosenPokemon = pokemons.get(choice - 1);

            if (chosenPokemon.isKnockedOut()) {
                System.out.println(
                        chosenPokemon.getName()
                                + " är besegrad och kan inte strida."
                );
                continue;
            }

            return chosenPokemon;
        }
    }


    public static void showBattleStart(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        System.out.println(
                "\nDu valde: " + playerPokemon.getName()
        );

        System.out.println(
                "En vild " + wildPokemon.getName() + " dök upp!"
        );
    }

    public static void showMiss(
            Pokemon attacker,
            Attack attack
    ) {
        System.out.println(
                attacker.getName()
                        + " använder "
                        + attack.getName()
                        + " men missar!"
        );
    }

    public static void showAttackResult(
            Pokemon attacker,
            Pokemon defender,
            Attack attack,
            int damage,
            double effectiveness
    ) {
        System.out.println(
                attacker.getName()
                        + " använder "
                        + attack.getName()
                        + " på "
                        + defender.getName()
                        + " och gör "
                        + damage
                        + " skada!"
        );

        if (effectiveness > 1.0) {
            System.out.println("Det är supereffektivt!");
        } else if (effectiveness < 1.0) {
            System.out.println("Det är inte effektivt...");
        }

    }

    public static void showKnockedOut(Pokemon pokemon) {
        System.out.println(
                pokemon.getName() + " svimmade!"
        );
    }

    public static void showBattleResult(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        if (wildPokemon.isKnockedOut()) {
            System.out.println(
                    "Du vann! "
                            + playerPokemon.getName()
                            + " besegrade "
                            + wildPokemon.getName()
                            + "."
            );
        } else if (playerPokemon.isKnockedOut()) {
            System.out.println(
                    "Du förlorade... "
                            + wildPokemon.getName()
                            + " besegrade "
                            + playerPokemon.getName()
                            + "."
            );
        }
    }

    public static void showBattleStats(BattleStats stats) {
        System.out.println("\n=== RESULTAT ===");
        System.out.println("Totalt antal strider: " + stats.getTotalBattles());
        System.out.println("Vinster: " + stats.getWins());
        System.out.println("Förluster: " + stats.getLosses());
    }
}