package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Ansvarar för menyer och presentation av information under strider.
// Här visas bland annat handlingar, Pokemon, attackresultat och statistik.
// Själva stridslogiken hanteras av BattleService och Battle.
public class BattleMenu {

    // Visar vilka handlingar spelaren kan välja under en strid.
    // Returnerar den valda handlingen som BattleAction.
    public static BattleAction chooseAction(
            Pokemon pokemon,
            Scanner scanner
    ) {
        // Samlar Pokemonens attacker i en lista av BattleAction.
        // Det fungerar eftersom Attack implementerar BattleAction.
        List<BattleAction> actions =
                new ArrayList<>(pokemon.getAttacks());

        // Lägger till flykt som ett alternativ i samma lista.
        // Polymorfism gör att attacker och flykt kan hanteras gemensamt.
        actions.add(new FleeAction());

        ConsoleUI.header("VÄLJ HANDLING");

        // Går igenom alla handlingar och visar deras namn.
        // label() anropar rätt implementation för varje handling.
        for (int i = 0; i < actions.size(); i++) {
            System.out.println(
                    (i + 1) + ". " + actions.get(i).label()
            );
        }

        // Läser in ett giltigt val inom antalet tillgängliga handlingar.
        int choice = InputHelper.readIntInRange(
                scanner,
                "Välj handling: ",
                1,
                actions.size()
        );

        // Hämtar och returnerar den valda handlingen.
        // Minus ett behövs eftersom listans index börjar på noll.
        return actions.get(choice - 1);
    }

    // Låter spelaren välja vilken Pokemon som ska användas i striden.
    public static Pokemon choosePlayerPokemon(
            List<Pokemon> pokemons,
            Scanner scanner
    ) {
        // Kontrollerar att det finns Pokemon att välja mellan.
        if (pokemons == null || pokemons.isEmpty()) {
            throw new IllegalArgumentException(
                    "Det finns inga Pokemon att välja."
            );
        }

        // Använder Stream för att kontrollera om minst en Pokemon
        // fortfarande har HP kvar och kan delta i striden.
        boolean hasAvailablePokemon = pokemons.stream()
                .anyMatch(pokemon ->
                        pokemon != null && !pokemon.isKnockedOut()
                );

        // Förhindrar att spelaren hamnar i en meny
        // där ingen Pokemon går att välja.
        if (!hasAvailablePokemon) {
            throw new IllegalArgumentException(
                    "Det finns inga stridsdugliga Pokemon."
            );
        }

        // Fortsätter visa valmenyn tills en stridsduglig Pokemon väljs.
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

            // Läser in ett giltigt nummer från Pokemon-listan.
            int choice = InputHelper.readIntInRange(
                    scanner,
                    "Välj Pokemon: ",
                    1,
                    pokemons.size()
            );

            // Hämtar Pokemon som motsvarar spelarens val.
            Pokemon chosenPokemon = pokemons.get(choice - 1);

            // En besegrad Pokemon får inte väljas till striden.
            // continue visar menyn igen om Pokemon saknar HP.
            if (chosenPokemon.isKnockedOut()) {
                Messages.warning(
                        chosenPokemon.getName()
                                + " är besegrad och kan inte strida."
                );
                continue;
            }

            // Returnerar Pokemon när valet är godkänt.
            return chosenPokemon;
        }
    }

    // Visar vilka Pokemon som möts när en ny strid börjar.
    public static void showBattleStart(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        // Informerar spelaren om vilken Pokemon som valts.
        System.out.println(
                "\nDu valde: " + playerPokemon.getName()
        );

        // Visar namnet på den slumpmässigt valda motståndaren.
        System.out.println(
                "En vild " + wildPokemon.getName() + " dök upp!"
        );
    }

    // Visar ett meddelande när en attack inte träffar.
    public static void showMiss(
            Pokemon attacker,
            Attack attack
    ) {
        // Använder Messages för att visa en varning i konsolen.
        Messages.warning(
                attacker.getName()
                        + " använder "
                        + attack.getName()
                        + " men missar!"
        );
    }

    // Tolkar resultatet av en attack och väljer vad som ska visas.
    public static void showAttackResult(
            Pokemon attacker,
            Pokemon defender,
            Attack attack,
            AttackResult result
    ) {
        // Kontrollerar om attacken missade.
        // Vid miss visas endast ett meddelande och metoden avslutas.
        if (!result.hit()) {
            showMiss(attacker, attack);
            return;
        }

        // Visar ett särskilt meddelande om attacken blev kritisk.
        if (result.criticalHit()) {
            showCriticalHit();
        }

        // Skickar skadan och typfördelen vidare till den andra
        // versionen av showAttackResult för själva utskriften.
        // Detta är metodöverlagring eftersom metoderna har samma namn
        // men olika parametrar.
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
        // Skriver ut vilken Pokemon som attackerade,
        // vilken attack som användes och hur mycket skada den gjorde.
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

        // Visar om attacken hade typfördel eller typnackdel.
        // En multiplikator på 1.0 innebär normal effektivitet.
        if (effectiveness > 1.0) {
            Messages.success("Det är supereffektivt!");
        } else if (effectiveness < 1.0) {
            Messages.warning("Det är inte effektivt...");
        }
    }

    // Visar vilken Pokemon som blev besegrad under striden.
    public static void showKnockedOut(Pokemon pokemon) {
        Messages.warning(
                pokemon.getName() + " svimmade!"
        );
    }

    // Visar om spelaren vann eller förlorade striden.
    public static void showBattleResult(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        // Spelaren vinner om den vilda Pokemonen har noll HP.
        if (wildPokemon.isKnockedOut()) {
            Messages.success(
                    "Du vann! "
                            + playerPokemon.getName()
                            + " besegrade "
                            + wildPokemon.getName()
                            + "."
            );

            // Spelaren förlorar om den egna Pokemonen har noll HP.
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

    // Visar den totala statistiken från spelarens strider.
    public static void showBattleStats(BattleStats stats) {

        // Samlar utskriftsraderna i en lista innan de visas.
        // Detta gör att ConsoleUI kan skriva ut allt i samma ruta.
        List<String> lines = new ArrayList<>();

        lines.add("");

        // Hämtar det totala antalet avslutade strider.
        lines.add(String.format(
                " %-25s %d",
                "Totalt antal strider",
                stats.getTotalBattles()
        ));

        // Lägger till en avdelare mellan totalen och resultaten.
        lines.add(" " + "─".repeat(30));

        // Visar antalet registrerade vinster.
        lines.add(String.format(
                " %-25s %d",
                "Vinster",
                stats.getWins()
        ));

        // Visar antalet registrerade förluster.
        lines.add(String.format(
                " %-25s %d",
                "Förluster",
                stats.getLosses()
        ));

        // Visar hur många gånger spelaren har flytt.
        lines.add(String.format(
                " %-25s %d",
                "Flykter",
                stats.getFlees()
        ));

        lines.add("");

        // Skickar alla rader till ConsoleUI som skriver ut en ruta.
        // toArray omvandlar listan till den String-array som krävs.
        ConsoleUI.printBox(
                "TOTAL STRIDSSTATISTIK",
                lines.toArray(new String[0])
        );
    }

    // Visar vinster, förluster och beräknad statistik per Pokemon.
    public static void showPokemonStats(
            BattleStats stats,
            List<Pokemon> pokemons
    ) {
        // Skapar en lista som innehåller statistiktabellens rader.
        List<String> lines = new ArrayList<>();

        // Lägger till en rubrikrad med förkortningar för resultaten.
        lines.add("");
        lines.add(String.format(
                " %-12s %3s %3s %6s %5s",
                "Pokemon", "V", "F", "Vinst%", "Ratio"
        ));

        lines.add(" " + "─".repeat(36));

        // Håller reda på om någon Pokemon har statistik att visa.
        boolean hasStats = false;

        // Går igenom alla Pokemon och hämtar deras resultat via ID.
        for (Pokemon pokemon : pokemons) {
            int id = pokemon.getId();

            // getOrDefault returnerar noll om Pokemon saknar
            // registrerade vinster eller förluster.
            int wins = stats.getWinsByPokemon().getOrDefault(id, 0);
            int losses = stats.getLossesByPokemon().getOrDefault(id, 0);

            // Hoppar över Pokemon som inte har deltagit i någon
            // strid som slutat med vinst eller förlust.
            if (wins + losses == 0) {
                continue;
            }

            // Markerar att det finns minst en Pokemon med statistik.
            hasStats = true;

            // Hämtar förhållandet mellan vinster och förluster.
            double ratio = stats.getWinLossRatio(id);

            // Visar oändlighetssymbolen vid vinster utan förluster.
            // Annars formateras resultatet med två decimaler.
            String ratioText = Double.isInfinite(ratio)
                    ? "∞"
                    : String.format("%.2f", ratio);

            // Formaterar namnet, vinsterna, förlusterna,
            // vinstprocenten och förhållandet till en tabellrad.
            lines.add(String.format(
                    " %-12s %3d %3d %5.1f%% %5s",
                    pokemon.getName(),
                    wins,
                    losses,
                    stats.getWinPercentage(id),
                    ratioText
            ));
        }

        // Visar ett informationsmeddelande om ingen Pokemon
        // har några registrerade vinster eller förluster.
        if (!hasStats) {
            lines.add(" Ingen Pokemon har spelat ännu.");
        }

        lines.add("");

        // Visar den färdiga tabellen genom ConsoleUI.
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
        // Hämtar ID:t för den mest använda Pokemonen.
        int mostUsedId = stats.getMostUsedPokemonId();

        // Kontrollerar om någon Pokemon har använts tidigare.
        if (mostUsedId == -1) {
            Messages.warning("Ingen Pokémon har använts ännu.");
            return;
        }

        // Hämtar antalet gånger Pokemonen har använts.
        int usage = stats.getPokemonUsage().getOrDefault(mostUsedId, 0);

        // Söker efter Pokemonen med hjälp av Stream.
        // Optional hanterar både när Pokemonen finns
        // och när den tidigare har tagits bort ur Pokedex.
        pokemons.stream()
                .filter(pokemon -> pokemon.getId() == mostUsedId)
                .findFirst()
                .ifPresentOrElse(
                        pokemon -> {
                            // Visar namn och antal strider om Pokemonen finns.
                            System.out.println(
                                    "Mest använda Pokémon: " + pokemon.getName()
                                            + " | Antal strider: " + usage
                            );
                        },
                        () -> {
                            // Visar ID om Pokemonen har tagits bort.
                            // Statistiken finns fortfarande kvar.
                            System.out.println(
                                    "Mest använda Pokémon: ID " + mostUsedId
                                            + " (borttagen från Pokedex)"
                                            + " | Antal strider: " + usage
                            );
                        }
                );
    }

    // Visar en sorterad lista över spelarens mest använda attacker.
    public static void showMostUsedAttack(BattleStats stats) {

        // Hämtar användningsstatistiken där attacknamnet är nyckeln
        // och antalet användningar är värdet.
        Map<String, Integer> attackUsage = stats.getAttackUsage();

        // Avslutar metoden om ingen attack har registrerats.
        if (attackUsage.isEmpty()) {
            Messages.warning("Ingen attack har använts ännu.");
            return;
        }

        // Skapar en lista för att samla den formaterade statistiken.
        List<String> lines = new ArrayList<>();

        lines.add("");

        // Går igenom alla attacker med hjälp av en Stream.
        attackUsage.entrySet()
                .stream()

                // Sorterar först efter antal användningar, högst först.
                // Vid lika många användningar sorteras namnen alfabetiskt.
                .sorted(
                        Map.Entry.<String, Integer>comparingByValue()
                                .reversed()
                                .thenComparing(Map.Entry.comparingByKey())
                )

                // Formaterar varje attack och lägger till den i utskriftslistan.
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

        // Visar den sorterade statistiken i en informationsruta.
        ConsoleUI.printBox(
                "MEST ANVÄNDA ATTACKER",
                lines.toArray(new String[0])
        );
    }

    // Visar ett meddelande när en attack blir kritisk.
    public static void showCriticalHit() {
        Messages.warning("Kritisk träff!");
    }

    // Visar båda Pokemonens nuvarande HP efter en handling.
    // Använder en grafisk HP-mätare tillsammans med exakta HP-värden.
    public static void showBattleHp(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        System.out.println();

        // Skriver ut spelarens Pokemon med namn, HP-mätare och hälsa.
        System.out.printf(
                "%s %s %d/%d HP%n",
                playerPokemon.getName(),
                createHpBar(playerPokemon.getCurrentHp(), playerPokemon.getMaxHp()),
                playerPokemon.getCurrentHp(),
                playerPokemon.getMaxHp()
        );

        // Skriver ut motståndarens Pokemon på samma sätt.
        System.out.printf(
                "%s %s %d/%d HP%n",
                wildPokemon.getName(),
                createHpBar(wildPokemon.getCurrentHp(), wildPokemon.getMaxHp()),
                wildPokemon.getCurrentHp(),
                wildPokemon.getMaxHp()
        );
    }

    // Skapar en grafisk HP-mätare som består av tio positioner.
    // Metoden är privat eftersom den bara används inom BattleMenu.
    private static String createHpBar(int currentHp, int maxHp) {

        // Bestämmer hur lång HP-mätaren ska vara.
        int barLength = 10;

        // Beräknar hur stor andel av HP-mätaren som ska fyllas.
        // Math.round avrundar till närmaste heltal.
        int filled = (int) Math.round(
                (double) currentHp / maxHp * barLength
        );

        // Begränsar antalet fyllda positioner till intervallet 0–10.
        // Detta förhindrar att ett ogiltigt antal tecken skrivs ut.
        filled = Math.clamp(filled, 0, barLength);

        // Skapar mätaren med fyllda och tomma positioner.
        // String.repeat används för att upprepa rätt antal tecken.
        return "[" + "█".repeat(filled)
                + "░".repeat(barLength - filled) + "]";
    }
}
