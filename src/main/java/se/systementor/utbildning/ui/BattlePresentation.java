package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.ActionResult;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.AttackResult;
import se.systementor.utbildning.model.BattleAction;
import se.systementor.utbildning.model.Pokemon;

// Hjälpklass som hanterar presentationen under strider och håller den separerad från stridslogiken.
public final class BattlePresentation {

    // Privat konstruktor eftersom klassen bara innehåller statiska metoder.
    private BattlePresentation() {
    }

    // Pausar mellan händelser så att spelaren ska hinna följa striden.
    public static void pause() {
        try {
            // Väntar två sekunder innan nästa händelse visas.
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            // Återställer avbrottsstatus så att annan kod kan upptäcka avbrottet.
            Thread.currentThread().interrupt();
        }
    }

    // Visar vems tur det är.
    public static void showTurn(Pokemon pokemon, boolean playerTurn) {

        // Villkorsoperatorn väljer rubrik beroende på vems tur det är.
        String title = playerTurn
                ? "SPELARENS TUR"
                : "CPU:ns TUR";

        System.out.println("\n════════ " + title + " ════════");
        System.out.println(pokemon.getName() + " gör sig redo...");
    }

    // Visar vem som attackerar först. Turordningen bestäms av Battle.
    public static void showFirstAttacker(Pokemon pokemon) {
        System.out.println(
                pokemon.getName() + " är snabbast och attackerar först!"
        );
    }

    // Visar ett meddelande när spelaren flyr från striden.
    public static void showFlee() {
        System.out.println("Du flydde från striden!");
    }

    // Använder BattleAction och ActionResult som gemensamma typer
    // för att hantera olika stridshandlingar (polymorfism).
    public static void showActionResult(
            BattleAction action,
            ActionResult result,
            Pokemon attacker,
            Pokemon defender
    ) {

        // instanceof kontrollerar typerna och ger tillgång till
        // attackResult och attack utan separat typomvandling.
        if (result instanceof AttackResult attackResult
                && action instanceof Attack attack) {

            BattleMenu.showAttackResult(
                    attacker,
                    defender,
                    attack,
                    attackResult
            );
        }
    }
}