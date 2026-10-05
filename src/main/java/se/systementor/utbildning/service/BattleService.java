package se.systementor.utbildning.service;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.ui.InputHelper;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BattleService {

    // Låter spelaren välja vilken Pokemon som ska användas i striden.
    public static Pokemon choosePlayerPokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        System.out.println("\n=== VÄLJ POKEMON ===");

        for (int i = 0; i < pokemons.size(); i++) {
            Pokemon pokemon = pokemons.get(i);

            System.out.println(
                    (i + 1) + ". " +
                            pokemon.getName() +
                            " | HP: " +
                            pokemon.getCurrentHp() +
                            "/" +
                            pokemon.getMaxHp()
            );
        }

        int choice = InputHelper.readIntInRange(
                scanner,
                "Välj Pokemon: ",
                1,
                pokemons.size()
        );

        return pokemons.get(choice - 1);
    }


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
        Pokemon playerPokemon = choosePlayerPokemon(pokemons, scanner);
        Pokemon wildPokemon = chooseRandomWildPokemon(pokemons);

        System.out.println("\nDu valde: " + playerPokemon.getName());
        System.out.println("En vild " + wildPokemon.getName() + " dök upp!");
    }
}