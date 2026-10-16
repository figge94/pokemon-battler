package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Type;

import java.util.Scanner;

// Hjälpklass för att läsa och validera användarinmatning.
public final class InputHelper {

    // Privat konstruktor eftersom alla metoder är statiska.
    private InputHelper() {
    }

    // Läser in ett heltal inom ett angivet intervall.
    public static int readIntInRange(
            Scanner scanner,
            String prompt,
            int min,
            int max
    ) {
        // Förhindrar ett ogiltigt intervall.
        if (min > max) {
            throw new IllegalArgumentException(
                    "Minsta värdet får inte vara större än högsta värdet."
            );
        }

        // Fortsätter fråga tills användaren anger ett giltigt tal.
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            try {
                // Försöker omvandla texten till ett heltal.
                int value = Integer.parseInt(input);

                if (value < min || value > max) {
                    System.out.println(
                            "Värdet måste vara mellan " + min + " och " + max + "."
                    );
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                // Hanterar inmatning som inte är ett heltal.
                System.out.println("Du måste skriva ett heltal.");
            }
        }
    }

    // Låter användaren välja en Pokemon-typ.
    public static Type chooseType(Scanner scanner) {

        // Hämtar alla tillgängliga typer från enum.
        Type[] types = Type.values();

        System.out.println("Välj typ:");

        // Skriver ut alla typer med ett nummer framför varje alternativ.
        // Nummereringen börjar på 1 för att vara tydlig för användaren.
        for (int i = 0; i < types.length; i++) {
            // Visar typens svenska namn i stället för enum-konstanten.
            System.out.println((i + 1) + ": " + types[i].getLabel());
        }

        // Återanvänder valideringen för heltal.
        int choice = readIntInRange(
                scanner,
                "Val: ",
                1,
                types.length
        );

        // -1 eftersom arrayens index börjar på 0.
        return types[choice - 1];
    }

    // Läser in ett ja- eller nej-svar.
    public static boolean readYesNo(Scanner scanner, String prompt) {

        while (true) {
            System.out.print(prompt);
            // trim tar bort mellanslag i början och slutet.
            String input = scanner.nextLine().trim();

            // Ignorerar stora och små bokstäver.
            if (input.equalsIgnoreCase("ja")) {
                return true;
            }

            if (input.equalsIgnoreCase("nej")) {
                return false;
            }

            System.out.println("Skriv ja eller nej.");
        }
    }

    // Läser in ett namn som följer programmets namnregler.
    public static String readValidName(Scanner scanner, String prompt) {

        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                System.out.println("Namnet får inte vara tomt.");
                continue;
            }

            // Tillåter endast bokstäver och mellanslag.
            if (!isValidName(input)) {
                System.out.println(
                        "Namnet får bara innehålla bokstäver och mellanslag."
                );
                continue;
            }

            return input;
        }
    }

    // Kontrollerar att namnet endast innehåller tillåtna tecken.
    private static boolean isValidName(String name) {
        return name.matches("[a-zA-ZåäöÅÄÖ ]+");
    }
}