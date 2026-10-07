package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;
import se.systementor.utbildning.service.AttackService;
import se.systementor.utbildning.service.PokemonService;

import java.util.Scanner;

public class AttackMenu {

    public static void handleAddAttack(
            Pokemon pokemon,
            Scanner scanner
    ) {
        if (pokemon.getAttacks().size() >= 4) {
            System.out.println("En Pokemon kan ha max 4 attacker.");
            return;
        }

        String attackName =
                InputHelper.readValidName(scanner, "Attacknamn: ");

        int damage =
                InputHelper.readIntInRange(
                        scanner,
                        "Skada: ",
                        1,
                        999
                );

        int accuracy =
                InputHelper.readIntInRange(
                        scanner,
                        "Träffsäkerhet (0-100): ",
                        0,
                        100
                );

        Type attackType =
                InputHelper.chooseType(scanner);

        Attack newAttack = new Attack(
                attackName,
                damage,
                accuracy,
                attackType
        );

        try {
            AttackService.addAttackToPokemon(
                    pokemon,
                    newAttack
            );

            System.out.println(
                    pokemon.getName()
                            + " har fått attacken "
                            + newAttack.getName()
            );

        } catch (InvalidAttackException e) {
            System.out.println("Fel: " + e.getMessage());
        }

        PokemonService.printDetails(pokemon);
    }
}
