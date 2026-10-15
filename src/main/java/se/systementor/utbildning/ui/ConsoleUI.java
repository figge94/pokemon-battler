
package se.systementor.utbildning.ui;

// Hjälpklass som ansvarar för att skriva ut rubriker,
// menyer och informationsrutor i konsolen.
// Genom att samla utskrifterna här slipper andra klasser
// duplicera kod för färger och ramar.
public final class ConsoleUI {

    // Standardbredden som används för menyer och informationsrutor.
    private static final int MENU_WIDTH = 40;

    // Privat konstruktor förhindrar att objekt av klassen skapas.
    // Metoderna är statiska och kan anropas direkt via ConsoleUI.
    private ConsoleUI() {
    }

    // Skriver ut en rubrik med en ram som anpassas efter textens längd.
    public static void header(String title) {

        // Skapar en kant som är fyra tecken längre än rubriken
        // för att ge plats åt mellanrum på båda sidor.
        String border = "═".repeat(title.length() + 4);

        // Skriver ut rubrikens övre kant, text och nedre kant.
        // Färgen återställs efter utskriften.
        System.out.println(ConsoleColors.CYAN
                + "╔" + border + "╗");
        System.out.println("║  " + title + "  ║");
        System.out.println("╚" + border + "╝"
                + ConsoleColors.RESET);
    }

    // Skriver ut en meny med rubrik och valfri mängd alternativ.
    // Färgen och alternativen skickas in från den anropande klassen.
    public static void printMenu(
            String title,
            String color,
            String... options
    ) {
        // Skapar en ram med den förbestämda menybredden.
        String border = "═".repeat(MENU_WIDTH);

        // Skriver ut menyns övre kant, rubrik och avdelare.
        // printf används för att placera texten inom samma bredd.
        System.out.println(color + "╔" + border + "╗");
        System.out.printf("║ %-" + (MENU_WIDTH - 2) + "s ║%n", title);
        System.out.println("╠" + border + "╣");

        // Går igenom alla menyalternativ och skriver ut dem på egna rader.
        // String... gör att metoden kan ta emot olika många alternativ.
        for (String option : options) {
            System.out.printf(
                    "║ %-" + (MENU_WIDTH - 2) + "s ║%n",
                    option
            );
        }

        // Avslutar menyn med en nedre kant och återställer färgen.
        System.out.println("╚" + border + "╝" + ConsoleColors.RESET);
    }

    // Skriver ut en informationsruta med rubrik och valfritt antal rader.
    // Till skillnad från printMenu kan rutans bredd anpassas efter innehållet.
    public static void printBox(String title, String... lines) {

        // Börjar med standardbredden så att rutan inte blir för smal.
        int width = MENU_WIDTH;

        // Jämför varje rads längd med rutans nuvarande bredd.
        // Math.max väljer det största värdet så att texten får plats.
        for (String line : lines) {
            width = Math.max(width, line.length() + 2);
        }

        // Kontrollerar även rubrikens längd så att den får plats.
        width = Math.max(width, title.length() + 2);

        // Skapar en ram utifrån den slutliga bredden.
        String border = "═".repeat(width);

        // Skriver ut rutans övre kant, rubrik och avdelare.
        System.out.println(ConsoleColors.CYAN + "╔" + border + "╗");
        System.out.printf("║ %-" + (width - 2) + "s ║%n", title);
        System.out.println("╠" + border + "╣");

        // Skriver ut samtliga textrader inom samma ram.
        // Formateringen vänsterjusterar texten och fyller ut med mellanslag.
        for (String line : lines) {
            System.out.printf(
                    "║ %-" + (width - 2) + "s ║%n",
                    line
            );
        }

        // Skriver ut den nedre kanten och återställer konsolfärgen.
        System.out.println("╚" + border + "╝" + ConsoleColors.RESET);
    }
}
