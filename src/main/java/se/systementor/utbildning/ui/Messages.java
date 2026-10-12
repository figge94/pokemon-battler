package se.systementor.utbildning.ui;

public final class Messages {

    private Messages() {
    }

    // Visar ett meddelande när något lyckas.
    public static void success(String message) {
        System.out.println(
                ConsoleColors.GREEN + message + ConsoleColors.RESET
        );
    }

    // Visar ett felmeddelande.
    public static void error(String message) {
        System.out.println(
                ConsoleColors.RED + message + ConsoleColors.RESET
        );
    }

    // Visar en varning.
    public static void warning(String message) {
        System.out.println(
                ConsoleColors.YELLOW + message + ConsoleColors.RESET
        );
    }

    // Visar ett informationsmeddelande.
    public static void info(String message) {
        System.out.println(
                ConsoleColors.CYAN + message + ConsoleColors.RESET
        );
    }
}