package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.AttackResult;
import se.systementor.utbildning.model.BattleAction;
import se.systementor.utbildning.model.FleeAction;
import se.systementor.utbildning.model.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Hanterar spelarens val och visar vad som händer under strider.
// Själva stridslogiken hanteras av BattleService och Battle.
public class BattleMenu {

    // Låter spelaren välja en handling under striden.
    public static BattleAction chooseAction(
            Pokemon pokemon,
            Scanner scanner
    ) {
        // Attack implementerar BattleAction, så attacker och flykt
        // kan hanteras i samma lista genom polymorfism.
        List<BattleAction> actions =
                new ArrayList<>(pokemon.getAttacks());
        actions.add(new FleeAction());

        ConsoleUI.header("VÄLJ HANDLING");

        // label() anropar rätt implementation beroende på handling.
        for (int i = 0; i < actions.size(); i++) {
            System.out.println(
                    (i + 1) + ". " + actions.get(i).label()
            );
        }

        int choice = InputHelper.readIntInRange(
                scanner,
                "Välj handling: ",
                1,
                actions.size()
        );

        // Menyval börjar på 1, medan listans index börjar på 0.
        return actions.get(choice - 1);
    }

    // Låter spelaren välja Pokemon som ska användas i striden.
    public static Pokemon choosePlayerPokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        if (pokemons == null || pokemons.isEmpty()) {
            throw new IllegalArgumentException(
                    "Det finns inga Pokemon att välja."
            );
        }

        // Stream och anyMatch kontrollerar om minst en Pokemon kan strida.
        boolean hasAvailablePokemon = pokemons.stream()
                .anyMatch(pokemon ->
                        pokemon != null && !pokemon.isKnockedOut()
                );

        if (!hasAvailablePokemon) {
            throw new IllegalArgumentException(
                    "Det finns inga stridsdugliga Pokemon."
            );
        }

        // Fortsätter visa menyn tills en stridsduglig Pokemon väljs.
        while (true) {
            ConsoleUI.header("VÄLJ POKEMON");

            // Visar alla Pokemon med namn och aktuell hälsa.
            // Nummereringen börjar på ett för användarens skull.
            for (int i = 0; i < pokemons.size(); i++) {
                Pokemon pokemon = pokemons.get(i);

                System.out.println(
                        (i + 1) + ". "
                                + pokemon.getName()
                                + " | HP: "
                                + pokemon.getCurrentHp()
                                + "/"
                                + pokemon.getMaxHp()
                );
            }

            int choice = InputHelper.readIntInRange(
                    scanner,
                    "Välj Pokemon: ",
                    1,
                    pokemons.size()
            );

            Pokemon chosenPokemon = pokemons.get(choice - 1);

            // continue hoppar tillbaka till början av loopen vid en utslagen Pokemon.
            if (chosenPokemon.isKnockedOut()) {
                Messages.warning(
                        chosenPokemon.getName()
                                + " är besegrad och kan inte strida."
                );
                continue;
            }
            return chosenPokemon;
        }
    }

    // Visar vilka Pokemon som möts i striden.
    public static void showBattleStart(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        System.out.println(
                "\nDu valde: " + playerPokemon.getName()
        );

        System.out.println(
                "En vild " + wildPokemon.getName() + " dök upp!"
        );
    }

    // Visar ett meddelande när en attack missar.
    public static void showMiss(
            Pokemon attacker,
            Attack attack
    ) {
        Messages.warning(
                attacker.getName()
                        + " använder "
                        + attack.getName()
                        + " men missar!"
        );
    }

    // Tolkar ett AttackResult och visar resultatet av attacken.
    public static void showAttackResult(
            Pokemon attacker,
            Pokemon defender,
            Attack attack,
            AttackResult result
    ) {
        // Kontrollerar om attacken missade.
        if (!result.hit()) {
            showMiss(attacker, attack);
            return;
        }

        // Visar ett meddelande om attacken blev kritisk.
        if (result.criticalHit()) {
            showCriticalHit();
        }

        // Metodöverlagring: anropar metoden med samma namn men andra parametrar.
        showAttackResult(
                attacker,
                defender,
                attack,
                result.damage(),
                result.effectiveness()
        );
    }

    // Visar hur mycket skada en träffande attack orsakade.
    public static void showAttackResult(
            Pokemon attacker,
            Pokemon defender,
            Attack attack,
            int damage,
            double effectiveness
    ) {
        System.out.println(
                attacker.getName()
                        + " använder "
                        + attack.getName()
                        + " på "
                        + defender.getName()
                        + " och gör "
                        + damage
                        + " skada!"
        );

        if (effectiveness > 1.0) {
            Messages.success("Det är supereffektivt!");
        } else if (effectiveness < 1.0) {
            Messages.warning("Det är inte effektivt...");
        }
    }

    // Visar när en Pokemon blir utslagen.
    public static void showKnockedOut(Pokemon pokemon) {
        Messages.warning(
                pokemon.getName() + " svimmade!"
        );
    }

    // Visar om spelaren vann eller förlorade.
    public static void showBattleResult(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        if (wildPokemon.isKnockedOut()) {
            Messages.success(
                    "Du vann! "
                            + playerPokemon.getName()
                            + " besegrade "
                            + wildPokemon.getName()
                            + "."
            );

        } else if (playerPokemon.isKnockedOut()) {
            Messages.error(
                    "Du förlorade... "
                            + wildPokemon.getName()
                            + " besegrade "
                            + playerPokemon.getName()
                            + "."
            );
        }
    }

    // Visar ett meddelande vid en kritisk träff.
    public static void showCriticalHit() {
        Messages.warning("Kritisk träff!");
    }

    // Visar båda Pokemons hälsa med HP-mätare.
    public static void showBattleHp(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        System.out.println();

        System.out.printf(
                "%s %s %d/%d HP%n",
                playerPokemon.getName(),
                createHpBar(playerPokemon.getCurrentHp(), playerPokemon.getMaxHp()),
                playerPokemon.getCurrentHp(),
                playerPokemon.getMaxHp()
        );

        System.out.printf(
                "%s %s %d/%d HP%n",
                wildPokemon.getName(),
                createHpBar(wildPokemon.getCurrentHp(), wildPokemon.getMaxHp()),
                wildPokemon.getCurrentHp(),
                wildPokemon.getMaxHp()
        );
    }

    // Skapar en HP-mätare med tio positioner.
    private static String createHpBar(int currentHp, int maxHp) {
        int barLength = 10;

        // Beräknar andelen fyllda positioner och avrundar.
        int filled = (int) Math.round(
                (double) currentHp / maxHp * barLength
        );

        // Begränsar värdet till intervallet 0–10.
        filled = Math.clamp(filled, 0, barLength);

        // String.repeat skapar fyllda och tomma positioner.
        return "[" + "█".repeat(filled)
                + "░".repeat(barLength - filled) + "]";
    }
}
