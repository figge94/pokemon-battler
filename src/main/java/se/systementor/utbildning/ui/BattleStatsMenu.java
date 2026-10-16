package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Visar statistik från spelarens strider.
public final class BattleStatsMenu {

    // Privat konstruktor eftersom klassen bara innehåller statiska metoder.
    private BattleStatsMenu() {
    }

    // Visar den totala statistiken från spelarens strider.
    public static void showBattleStats(BattleStats stats) {
        List<String> lines = new ArrayList<>();

        lines.add("");

        lines.add(String.format(
                " %-25s %d",
                "Totalt antal strider",
                stats.getTotalBattles()
        ));

        lines.add(" " + "─".repeat(30));

        lines.add(String.format(
                " %-25s %d",
                "Vinster",
                stats.getWins()
        ));

        lines.add(String.format(
                " %-25s %d",
                "Förluster",
                stats.getLosses()
        ));

        lines.add(String.format(
                " %-25s %d",
                "Flykter",
                stats.getFlees()
        ));

        lines.add("");

        // toArray omvandlar listan till en String-array för ConsoleUI.
        ConsoleUI.printBox(
                "TOTAL STRIDSSTATISTIK",
                lines.toArray(new String[0])
        );
    }

    // Visar vinster, förluster och statistik per Pokemon.
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

            // getOrDefault returnerar 0 om ingen statistik finns för ID:t.
            int wins = stats.getWinsByPokemon().getOrDefault(id, 0);
            int losses = stats.getLossesByPokemon().getOrDefault(id, 0);

            if (wins + losses == 0) {
                continue;
            }

            hasStats = true;

            double ratio = stats.getWinLossRatio(id);

            // Visar ∞ om Pokemonen har vinster men inga förluster.
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

    // Visar vilken Pokemon spelaren har använt flest gånger.
    public static void showMostUsedPokemon(
            BattleStats stats,
            List<Pokemon> pokemons
    ) {
        int mostUsedId = stats.getMostUsedPokemonId();

        if (mostUsedId == -1) {
            Messages.warning("Ingen Pokemon har använts ännu.");
            return;
        }

        int usage = stats.getPokemonUsage().getOrDefault(mostUsedId, 0);

        // Optional hanterar även om Pokemon har tagits bort ur Pokedex.
        pokemons.stream()
                .filter(pokemon -> pokemon.getId() == mostUsedId)
                .findFirst()
                .ifPresentOrElse(
                        pokemon -> {
                            System.out.println(
                                    "Mest använda Pokemon: " + pokemon.getName()
                                            + " | Antal strider: " + usage
                            );
                        },
                        () -> {
                            // Statistiken behålls även om Pokemonen tagits bort.
                            System.out.println(
                                    "Mest använda Pokemon: ID " + mostUsedId
                                            + " (borttagen från Pokedex)"
                                            + " | Antal strider: " + usage
                            );
                        }
                );
    }

    // Visar attacker sorterade efter hur ofta de har använts.
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

                // Sorterar först efter antal användningar och sedan namn.
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
}
