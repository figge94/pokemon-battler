package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;

import java.util.List;
import java.util.Scanner;

public class BattleMenu {

    public static Attack chooseAttack(
            Pokemon pokemon,
            Scanner scanner
    ) {
        List<Attack> attacks = pokemon.getAttacks();

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
}