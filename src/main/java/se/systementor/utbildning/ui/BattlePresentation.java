package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Pokemon;

public final class BattlePresentation {

    private BattlePresentation() {
        // Klassen ska inte instansieras.
    }

    // Ger spelaren tid att följa striden.
    public static void pause() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void showTurn(Pokemon pokemon, boolean playerTurn) {

        // Visar tydligt vem som ska attackera.
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
}
