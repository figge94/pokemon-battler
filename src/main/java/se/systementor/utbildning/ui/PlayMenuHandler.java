package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.service.BattleService;

import java.util.List;
import java.util.Scanner;

// Hanterar spelmenyn för strider och statistik.
public class PlayMenuHandler {
    private final BattleService battleService;

    // Tar emot BattleService via konstruktorn (Dependency Injection).
    public PlayMenuHandler(BattleService battleService) {
        this.battleService = battleService;
    }

    // Låter spelaren välja Pokemon och startar en strid.
    private void startBattle(List<Pokemon> pokemons, Scanner scanner) {
        try {
            Pokemon playerPokemon = BattleMenu.choosePlayerPokemon(
                    pokemons, scanner
            );

            battleService.startBattle(pokemons, playerPokemon, scanner);

        } catch (IllegalArgumentException e) {
            Messages.error(e.getMessage());
        }
    }

    // Visar spelmenyn tills användaren väljer att gå tillbaka.
    public void show(List<Pokemon> pokemons, Scanner scanner) {
        boolean inPlayMenu = true;

        // Visar menyn tills användaren väljer att gå tillbaka.
        while (inPlayMenu) {
            Menu.showPlayMenu();

            int choice = InputHelper.readIntInRange(
                    scanner, "Välj: ", 1, 6
            );

            switch (choice) {
                case 1 -> startBattle(pokemons, scanner);

                // Visar det totala antalet strider, vinster, förluster och flykter.
                case 2 -> BattleStatsMenu.showBattleStats(
                        battleService.getBattleStats()
                );

                // Visar vinster, förluster och vinstprocent per Pokemon.
                case 3 -> BattleStatsMenu.showPokemonStats(
                        battleService.getBattleStats(),
                        pokemons
                );

                // Visar vilken Pokemon spelaren har använt flest gånger.
                case 4 -> BattleStatsMenu.showMostUsedPokemon(
                        battleService.getBattleStats(),
                        pokemons
                );

                // Visar spelarens attacker sorterade efter antal användningar.
                case 5 -> BattleStatsMenu.showMostUsedAttack(
                        battleService.getBattleStats()
                );

                // Går tillbaka till huvudmenyn.
                case 6 -> inPlayMenu = false;
            }
        }
    }
}