
package se.systementor.utbildning.ui;

// Hjälpklass för att skriva ut rubriker, menyer och informationsrutor.
public final class ConsoleUI {

    // Standardbredd för menyer och informationsrutor.
    private static final int MENU_WIDTH = 40;

    // Privat konstruktor eftersom klassen bara har statiska metoder.
    private ConsoleUI() {
    }

    // Skriver ut en rubrik med en ram anpassad efter textens längd.
    public static void header(String title) {

        // Lägger till plats för två mellanslag på varje sida.
        String border = "═".repeat(title.length() + 4);

        System.out.println(ConsoleColors.CYAN
                + "╔" + border + "╗");
        System.out.println("║  " + title + "  ║");
        System.out.println("╚" + border + "╝"
                + ConsoleColors.RESET);
    }

    // Skriver ut en meny med valfri färg och antal alternativ.
    public static void printMenu(
            String title,
            String color,
            String... options
    ) {
        printBoxHeader(title, color, MENU_WIDTH);

        for (String option : options) {
            printLine(option, MENU_WIDTH);
        }

        printBoxFooter(MENU_WIDTH);
    }

    // Skriver ut en informationsruta som anpassas efter innehållet.
    public static void printBox(String title, String... lines) {

        // Börjar med standardbredden.
        int width = MENU_WIDTH;

        // Anpassar bredden efter innehållet.
        for (String line : lines) {
            width = Math.max(width, line.length() + 2);
        }

        // Kontrollerar även att rubriken får plats.
        width = Math.max(width, title.length() + 2);

        printBoxHeader(title, ConsoleColors.CYAN, width);

        for (String line : lines) {
            printLine(line, width);
        }

        printBoxFooter(width);
    }

    // Skriver ut rutans övre kant, rubrik och avdelare.
    private static void printBoxHeader(
            String title,
            String color,
            int width
    ) {
        String border = "═".repeat(width);

        System.out.println(color + "╔" + border + "╗");
        System.out.printf("║ %-" + (width - 2) + "s ║%n", title);
        System.out.println("╠" + border + "╣");
    }
    // Skriver ut en vänsterjusterad textrad inom ramen.
    private static void printLine(String text, int width) {
        System.out.printf(
                "║ %-" + (width - 2) + "s ║%n",
                text
        );
    }

    // Skriver ut rutans nedre kant och återställer textfärgen.
    private static void printBoxFooter(int width) {
        System.out.println(
                "╚" + "═".repeat(width) + "╝"
                        + ConsoleColors.RESET
        );
    }
}
