package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;
import se.systementor.utbildning.service.AttackService;

import java.util.List;
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

        PokemonMenu.printDetails(pokemon);
    }

    public static void removeAttackFromPokemon(
            Pokemon pokemon,
            Scanner scanner
    ) {
        List<Attack> attacks = pokemon.getAttacks();

        if (attacks.size() <= 1) {
            System.out.println("En Pokemon måste ha minst 1 attack.");
            return;
        }

        System.out.println("Välj attack att ta bort:");

        for (int i = 0; i < attacks.size(); i++) {
            System.out.println((i + 1) + ": " + attacks.get(i).getName());
        }

        int attackChoice = InputHelper.readIntInRange(
                scanner,
                "Välj: ",
                1,
                attacks.size()
        );

        Attack attackToRemove = attacks.get(attackChoice - 1);

        try {
            AttackService.removeAttackFromPokemon(
                    pokemon,
                    attackToRemove
            );

            System.out.println(
                    attackToRemove.getName() + " har tagits bort."
            );

        } catch (InvalidAttackException e) {
            System.out.println(
                    "Det gick inte att ta bort attacken. " + e.getMessage()
            );
        }

        System.out.println("\n=== FÖRHANDSVISNING ===");
        PokemonMenu.printDetails(pokemon);
    }
}
