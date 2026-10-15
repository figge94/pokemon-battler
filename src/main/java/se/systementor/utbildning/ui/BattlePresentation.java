package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.*;

public final class BattlePresentation {

    private BattlePresentation() {
    }

    // Ger spelaren tid att följa striden.
    public static void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void showTurn(Pokemon pokemon, boolean playerTurn) {
        // Visar vem som ska attackera.
        String title = playerTurn
                ? "SPELARENS TUR"
                : "CPU:ns TUR";

        System.out.println("\n════════ " + title + " ════════");
        System.out.println(pokemon.getName() + " gör sig redo...");
    }

    // Visar vilken Pokémon som attackerar först.
    public static void showFirstAttacker(Pokemon pokemon) {
        System.out.println(
                pokemon.getName() + " är snabbast och attackerar först!"
        );
    }

    // Visar att spelaren har lämnat striden.
    public static void showFlee() {
        System.out.println("Du flydde från striden!");
    }


    public static void showActionResult(
            BattleAction action,
            ActionResult result,
            Pokemon attacker,
            Pokemon defender
    ) {
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
