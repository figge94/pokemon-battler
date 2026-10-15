package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.service.BattleService;

import java.util.List;
import java.util.Scanner;

public class PlayMenuHandler {
    private final BattleService battleService;

    // Får stridshanteraren via konstruktorn.
    public PlayMenuHandler(BattleService battleService) {
        this.battleService = battleService;
    }

    // Hanterar spelmenyn tills användaren går tillbaka.
    public void show(List<Pokemon> pokemons, Scanner scanner) {
        boolean inPlayMenu = true;

        while (inPlayMenu) {
            Menu.showPlayMenu();

            int choice = InputHelper.readIntInRange(
                    scanner, "Välj: ", 1, 6
            );

            switch (choice) {
                // Startar striden via det injicerade BattleService-objektet.
                case 1 -> battleService.startBattle(pokemons, scanner);
                case 2 -> BattleMenu.showBattleStats(
                        battleService.getBattleStats()
                );
                case 3 -> BattleMenu.showPokemonStats(
                        battleService.getBattleStats(), pokemons
                );
                case 4 -> BattleMenu.showMostUsedPokemon(
                        battleService.getBattleStats(), pokemons
                );
                case 5 -> BattleMenu.showMostUsedAttack(
                        battleService.getBattleStats()
                );
                // Avslutar spelmenyn.
                case 6 -> inPlayMenu = false;
            }
        }
    }
}
