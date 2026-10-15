package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.service.BattleService;

import java.util.List;
import java.util.Scanner;

// Ansvarar för spelmenyn där användaren kan starta strider
// och visa olika typer av stridsstatistik.
// Själva stridslogiken hanteras av BattleService.
public class PlayMenuHandler {

    // Lagrar det BattleService-objekt som används i spelmenyn.
    // Samma objekt återanvänds så att aktuell statistik finns tillgänglig.
    private final BattleService battleService;

    // Tar emot BattleService via konstruktorn.
    // Detta är Dependency Injection eftersom beroendet skapas
    // utanför klassen och skickas in när menyhanteraren skapas.
    public PlayMenuHandler(BattleService battleService) {
        this.battleService = battleService;
    }

    // Visar spelmenyn och hanterar användarens val.
    // Menyn fortsätter visas tills användaren väljer att gå tillbaka.
    public void show(List<Pokemon> pokemons, Scanner scanner) {

        // Håller reda på om användaren fortfarande befinner sig i spelmenyn.
        boolean inPlayMenu = true;

        // Upprepar menyn tills användaren väljer alternativ 6.
        while (inPlayMenu) {

            // Skriver ut spelmenyn och läser in ett giltigt val.
            // InputHelper förhindrar att ogiltiga nummer accepteras.
            Menu.showPlayMenu();

            int choice = InputHelper.readIntInRange(
                    scanner, "Välj: ", 1, 6
            );

            // Skickar användaren vidare till rätt funktion.
            // Menyhanteraren ansvarar för valet, inte själva stridslogiken.
            switch (choice) {

                // Startar en ny strid mot en slumpmässig motståndare.
                case 1 -> {
                    try {
                        // Låter spelaren välja vilken Pokemon som ska användas.
                        // BattleMenu ansvarar för att visa alternativen
                        // och kontrollera att Pokemonen är stridsduglig.
                        Pokemon playerPokemon = BattleMenu.choosePlayerPokemon(
                                pokemons, scanner
                        );

                        // Startar striden med den valda Pokemonen.
                        // BattleService hanterar CPU, turordning och resultat.
                        battleService.startBattle(
                                pokemons,
                                playerPokemon,
                                scanner
                        );

                    } catch (IllegalArgumentException e) {

                        // Visar ett felmeddelande om striden inte kan startas.
                        // Exempelvis om inga giltiga Pokemon finns att välja.
                        Messages.error(e.getMessage());
                    }
                }

                // Visar det totala antalet strider, vinster, förluster och flykter.
                case 2 -> BattleMenu.showBattleStats(
                        battleService.getBattleStats()
                );

                // Visar vinster, förluster och vinstprocent per Pokemon.
                case 3 -> BattleMenu.showPokemonStats(
                        battleService.getBattleStats(),
                        pokemons
                );

                // Visar vilken Pokemon spelaren har använt flest gånger.
                case 4 -> BattleMenu.showMostUsedPokemon(
                        battleService.getBattleStats(),
                        pokemons
                );

                // Visar spelarens attacker sorterade efter antal användningar.
                case 5 -> BattleMenu.showMostUsedAttack(
                        battleService.getBattleStats()
                );

                // Avslutar spelmenyn och återgår till huvudmenyn.
                // while-loopen avslutas när inPlayMenu blir false.
                case 6 -> inPlayMenu = false;
            }
        }
    }
}