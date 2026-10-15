package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.*;

// Hjälpklass som ansvarar för presentationen under strider.
// Visar turordning, handlingarnas resultat och meddelanden.
// Genom att skilja presentation från stridslogik får
// BattleService ett tydligare ansvar.
public final class BattlePresentation {

    // Privat konstruktor eftersom klassen bara innehåller
    // statiska metoder och inte behöver skapas som objekt.
    private BattlePresentation() {
    }

    // Pausar programmet så att spelaren hinner följa striden.
    public static void pause() {

        try {
            // Väntar två sekunder innan nästa händelse visas.
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            // Återställer trådens avbrottsstatus om väntan avbryts.
            // Det gör att annan kod kan upptäcka avbrottet.
            Thread.currentThread().interrupt();
        }
    }

    // Visar vems tur det är att utföra en handling.
    public static void showTurn(Pokemon pokemon, boolean playerTurn) {

        // Väljer rubrik beroende på om det är spelarens eller CPU:ns tur.
        // Villkorsoperatorn används för att välja mellan två texter.
        String title = playerTurn
                ? "SPELARENS TUR"
                : "CPU:ns TUR";

        // Skriver ut turens rubrik och namnet på den Pokemon som agerar.
        System.out.println("\n════════ " + title + " ════════");
        System.out.println(pokemon.getName() + " gör sig redo...");
    }

    // Visar vilken Pokemon som ska utföra sin handling först.
    // Turordningen har redan beräknats av Battle.
    public static void showFirstAttacker(Pokemon pokemon) {
        System.out.println(
                pokemon.getName() + " är snabbast och attackerar först!"
        );
    }

    // Visar ett meddelande när spelaren har flytt från striden.
    // Själva flykten registreras av stridssystemet.
    public static void showFlee() {
        System.out.println("Du flydde från striden!");
    }

    // Tar emot resultatet av en stridshandling och visar rätt information.
    // BattleAction och ActionResult används som gemensamma typer
    // så att olika handlingar kan hanteras genom samma metod.
    public static void showActionResult(
            BattleAction action,
            ActionResult result,
            Pokemon attacker,
            Pokemon defender
    ) {

        // Kontrollerar om handlingen är en attack och resultatet
        // är ett AttackResult.
        // instanceof gör samtidigt att vi kan använda de
        // specifika typerna utan separat typomvandling.
        if (result instanceof AttackResult attackResult
                && action instanceof Attack attack) {

            // Låter BattleMenu visa skada, kritisk träff
            // och typfördel utifrån attackresultatet.
            BattleMenu.showAttackResult(
                    attacker,
                    defender,
                    attack,
                    attackResult
            );
        }
    }
}