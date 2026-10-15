package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BattleMenu {

    // Visar attacker spelaren kan välja mellan i en strid
    public static BattleAction chooseAction(
            Pokemon pokemon,
            Scanner scanner
    ) {
        // Samlar attacker och andra handlingar i samma lista.
        List<BattleAction> actions =
                new ArrayList<>(pokemon.getAttacks());

        actions.add(new FleeAction());

        System.out.println("\n=== VÄLJ HANDLING ===");

        // Menyn använder interfacet istället för konkreta klasser.
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

        return actions.get(choice - 1);
    }

    // Låter spelaren välja vilken Pokemon som ska användas i striden.
    public static Pokemon choosePlayerPokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        if (pokemons == null || pokemons.isEmpty()) {
            throw new IllegalArgumentException(
                    "Det finns inga Pokemon att välja."
            );
        }

        boolean hasAvailablePokemon = pokemons.stream()
                .anyMatch(pokemon ->
                        pokemon != null && !pokemon.isKnockedOut()
                );

        if (!hasAvailablePokemon) {
            throw new IllegalArgumentException(
                    "Det finns inga stridsdugliga Pokemon."
            );
        }

        while (true) {
            System.out.println("\n=== VÄLJ POKEMON ===");

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

    public static void showAttackResult(
            Pokemon attacker,
            Pokemon defender,
            Attack attack,
            AttackResult result
    ){
        // Visar om attacken missade.
        if (!result.hit()) {
            showMiss(attacker, attack);
            return;
        }

        if (result.criticalHit()) {
            showCriticalHit();
        }

        showAttackResult(
                attacker,
                defender,
                attack,
                result.damage(),
                result.effectiveness()
        );
    }

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

    public static void showKnockedOut(Pokemon pokemon) {
        Messages.warning(
                pokemon.getName() + " svimmade!"
        );
    }

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

    public static void showBattleStats(BattleStats stats) {
        List<String> lines = new ArrayList<>();

        lines.add("");

        lines.add(String.format(
                " %-25s %d",
                "Totalt antal strider",
                stats.getTotalBattles()
        ));

        lines.add(" " + "─".repeat(30));

        // Visar antal vinster.
        lines.add(String.format(
                " %-25s %d",
                "Vinster",
                stats.getWins()
        ));

        // Visar antal förluster.
        lines.add(String.format(
                " %-25s %d",
                "Förluster",
                stats.getLosses()
        ));

        // Visar antalet flykter.
        lines.add(String.format(
                " %-25s %d",
                "Flykter",
                stats.getFlees()
        ));

        lines.add("");

        ConsoleUI.printBox(
                "TOTAL STRIDSSTATISTIK",
                lines.toArray(new String[0])
        );
    }

    public static void showPokemonStats(
            BattleStats stats,
            List<Pokemon> pokemons
    ) {
        List<String> lines = new ArrayList<>();

        lines.add("");
        lines.add(String.format(
                " %-12s %3s %3s %6s %5s",
                "Pokemon", "V", "F", "Vinst%", "Ratio"
        ));

        lines.add(" " + "─".repeat(36));

        boolean hasStats = false;

        for (Pokemon pokemon : pokemons) {
            int id = pokemon.getId();

            int wins = stats.getWinsByPokemon().getOrDefault(id, 0);
            int losses = stats.getLossesByPokemon().getOrDefault(id, 0);

            if (wins + losses == 0) {
                continue;
            }

            hasStats = true;

            double ratio = stats.getWinLossRatio(id);

            String ratioText = Double.isInfinite(ratio)
                    ? "∞"
                    : String.format("%.2f", ratio);

            lines.add(String.format(
                    " %-12s %3d %3d %5.1f%% %5s",
                    pokemon.getName(),
                    wins,
                    losses,
                    stats.getWinPercentage(id),
                    ratioText
            ));
        }

        if (!hasStats) {
            lines.add(" Ingen Pokemon har spelat ännu.");
        }

        lines.add("");

        ConsoleUI.printBox(
                "STATISTIK PER POKEMON",
                lines.toArray(new String[0])
        );
    }

    public static void showMostUsedPokemon(
            BattleStats stats,
            List<Pokemon> pokemons
    ) {
        int mostUsedId = stats.getMostUsedPokemonId();

        Pokemon mostUsed = pokemons.stream()
                .filter(pokemon -> pokemon.getId() == mostUsedId)
                .findFirst()
                .orElse(null);

        if (mostUsed == null) {
            Messages.warning("Ingen Pokémon har använts ännu.");
            return;
        }

        int usage = stats.getPokemonUsage().getOrDefault(mostUsedId, 0);

        System.out.println(
                "Mest använda Pokémon: " + mostUsed.getName()
                        + " | Antal strider: " + usage
        );
    }

    public static void showMostUsedAttack(BattleStats stats) {
        Map<String, Integer> attackUsage = stats.getAttackUsage();

        if (attackUsage.isEmpty()) {
            Messages.warning("Ingen attack har använts ännu.");
            return;
        }

        List<String> lines = new ArrayList<>();

        lines.add("");

        attackUsage.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Integer>comparingByValue()
                                .reversed()
                                .thenComparing(Map.Entry.comparingByKey())
                )
                .forEach(entry ->
                        lines.add(
                                String.format(
                                        " %-20s | %d %s",
                                        entry.getKey(),
                                        entry.getValue(),
                                        entry.getValue() == 1 ? "gång" : "gånger"
                                )
                        )
                );

        lines.add("");

        ConsoleUI.printBox(
                "MEST ANVÄNDA ATTACKER",
                lines.toArray(new String[0])
        );
    }

    public static void showCriticalHit() {
        Messages.warning("Kritisk träff!");
    }

    // Visar båda Pokémonens HP efter en attack.

    public static void showBattleHp(Pokemon playerPokemon, Pokemon wildPokemon) {
        System.out.println();

        // Visar spelarens Pokémon med HP-mätare.
        System.out.printf(
                "%s %s %d/%d HP%n",
                playerPokemon.getName(),
                createHpBar(playerPokemon.getCurrentHp(), playerPokemon.getMaxHp()),
                playerPokemon.getCurrentHp(),
                playerPokemon.getMaxHp()
        );

        // Visar motståndarens Pokémon med HP-mätare.
        System.out.printf(
                "%s %s %d/%d HP%n",
                wildPokemon.getName(),
                createHpBar(wildPokemon.getCurrentHp(), wildPokemon.getMaxHp()),
                wildPokemon.getCurrentHp(),
                wildPokemon.getMaxHp()
        );
    }

    private static String createHpBar(int currentHp, int maxHp) {
        int barLength = 10;

        // Räknar ut hur stor del av HP-mätaren som ska vara fylld.
        int filled = (int) Math.round(
                (double) currentHp / maxHp * barLength
        );

        filled = Math.clamp(filled, 0, barLength);

        return "[" + "█".repeat(filled)
                + "░".repeat(barLength - filled) + "]";
    }
}