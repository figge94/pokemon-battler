package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.exception.PokemonSaveException;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;
import se.systementor.utbildning.service.AttackService;
import se.systementor.utbildning.service.PokemonService;

import java.util.List;
import java.util.Scanner;

public class AttackMenu {
    private final PokemonService pokemonService;

    public AttackMenu(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }
    public void handleAddAttack(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
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

        Attack newAttack = AttackService.createAttack(
                attackName,
                damage,
                accuracy,
                attackType
        );

        try {
            pokemonService.addAttackToPokemon(
                    pokemons,
                    pokemon,
                    newAttack
            );

            Messages.success(
                    pokemon.getName()
                            + " har fått attacken "
                            + newAttack.getName()
            );

        } catch (InvalidAttackException | PokemonSaveException e) {
            Messages.error("Fel: " + e.getMessage());
        }

        PokemonMenu.printDetails(pokemon);
    }

    public void removeAttackFromPokemon(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        List<Attack> attacks = pokemon.getAttacks();

        if (attacks.size() <= 1) {
            Messages.warning("En Pokemon måste ha minst 1 attack.");
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
            pokemonService.removeAttackFromPokemon(
                    pokemons,
                    pokemon,
                    attackToRemove
            );

            Messages.success(
                    attackToRemove.getName() + " har tagits bort."
            );

        } catch (InvalidAttackException | PokemonSaveException e) {
            Messages.error(
                    "Det gick inte att ta bort attacken. " + e.getMessage()
            );
        }

        System.out.println("\n=== FÖRHANDSVISNING ===");
        PokemonMenu.printDetails(pokemon);
    }
}
