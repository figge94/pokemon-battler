package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BattleMenu {

    // Visar attacker spelaren kan välja mellan i en strid
    public static Attack chooseAttack(
            Pokemon pokemon,
            Scanner scanner
    ) {
        List<Attack> attacks = pokemon.getAttacks();

        if (attacks.isEmpty()) {
            throw new IllegalArgumentException(
                    pokemon.getName() + " har inga attacker."
            );
        }

        System.out.println("\n=== VÄLJ ATTACK ===");

        for (int i = 0; i < attacks.size(); i++) {
            Attack attack = attacks.get(i);

            System.out.println(
                    (i + 1)
                            + ". "
                            + attack.getName()
                            + " | Skada: "
                            + attack.getBaseDamage()
            );
        }

        int choice = InputHelper.readIntInRange(
                scanner,
                "Välj attack: ",
                1,
                attacks.size()
        );

        return attacks.get(choice - 1);
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

    public static void showBattleStats(
            BattleStats stats,
            List<Pokemon> pokemons
    ) {
        List<String> lines = new ArrayList<>();

        lines.add("");

        lines.add(String.format(
                " %-25s %d",
                "Totalt antal strider",
                stats.getTotalBattles()
        ));

        lines.add(" " + "─".repeat(26));

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

        lines.add("");
        lines.add(" RESULTAT PER POKEMON");
        lines.add("");

        lines.add(String.format(
                " %-12s %3s %3s %9s",
                "Pokemon", "V", "F", "Vinst %"
        ));

        lines.add(" " + "─".repeat(30));

        boolean hasPokemonStats = false;

        for (Pokemon pokemon : pokemons) {
            int id = pokemon.getId();

            int wins = stats.getWinsByPokemon().getOrDefault(id, 0);
            int losses = stats.getLossesByPokemon().getOrDefault(id, 0);

            // Visar bara Pokemon som deltagit i minst en strid.
            if (wins + losses > 0) {
                hasPokemonStats = true;

                lines.add(String.format(
                        " %-12s %3d %3d %8.1f %%",
                        pokemon.getName(),
                        wins,
                        losses,
                        stats.getWinPercentage(id)
                ));
            }
        }

        if (!hasPokemonStats) {
            lines.add(" Ingen Pokemon har spelat ännu.");
        }

        lines.add("");

        ConsoleUI.printBox(
                "STRIDSSTATISTIK",
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


}