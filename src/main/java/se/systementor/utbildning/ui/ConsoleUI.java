package se.systementor.utbildning.ui;

public final class ConsoleUI {
    private static final int MENU_WIDTH = 40;

    private ConsoleUI() {
    }

    public static void header(String title) {
        String border = "═".repeat(title.length() + 4);

        System.out.println(ConsoleColors.CYAN
                + "╔" + border + "╗");
        System.out.println("║  " + title + "  ║");
        System.out.println("╚" + border + "╝"
                + ConsoleColors.RESET);
    }

    public static void printMenu(
            String title,
            String color,
            String... options
    ) {
        String border = "═".repeat(MENU_WIDTH);

        System.out.println(color + "╔" + border + "╗");
        System.out.printf("║ %-" + (MENU_WIDTH - 2) + "s ║%n", title);
        System.out.println("╠" + border + "╣");

        for (String option : options) {
            System.out.printf("║ %-" + (MENU_WIDTH - 2) + "s ║%n", option);
        }

        System.out.println("╚" + border + "╝" + ConsoleColors.RESET);
    }

    public static void printBox(String title, String... lines) {
        String border = "═".repeat(MENU_WIDTH);

        System.out.println(ConsoleColors.CYAN + "╔" + border + "╗");

        System.out.printf(
                "║ %-" + (MENU_WIDTH - 2) + "s ║%n",
                title
        );

        System.out.println("╠" + border + "╣");

        for (String line : lines) {
            System.out.printf(
                    "║ %-" + (MENU_WIDTH - 2) + "s ║%n",
                    line
            );
        }

        System.out.println("╚" + border + "╝" + ConsoleColors.RESET);
    }
}