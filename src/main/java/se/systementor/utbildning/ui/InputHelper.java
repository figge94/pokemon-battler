package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Type;

import java.util.Scanner;

public class InputHelper {

    // Läser in ett heltal inom ett angivet intervall.
    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        // Fortsätter fråga tills användaren skriver ett giltigt heltal.
        while (true) {
            System.out.print(prompt);

            String input = scanner.nextLine();

            try {
                // Försöker omvandla texten till ett heltal.
                int value = Integer.parseInt(input);

                // Kollar att talet ligger inom det tillåtna intervallet.
                if (value < min || value > max) {
                    System.out.println("Värdet måste vara mellan " + min + " och " + max + ".");
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Du måste skriva ett heltal.");
            }
        }
    }

    // Låter användaren välja en Pokemon-typ.
    public static Type chooseType(Scanner scanner) {
        // Hämtar alla värden från Type-enum.
        Type[] types = Type.values();

        System.out.println("Välj typ:");

        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ": " + types[i]);
        }

        int choice = readIntInRange(
                scanner,
                "Val: ",
                1,
                types.length
        );

        // Gör om användarens val från 1-baserat till rätt index i arrayen.
        return types[choice - 1];
    }

    // Läser in ja eller nej och returnerar true eller false.
    public static boolean readYesNo(Scanner scanner, String prompt) {
        // Fortsätter fråga tills användaren skriver ja eller nej.
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("ja")) {
                return true;
            }

            if (input.equalsIgnoreCase("nej")) {
                return false;
            }

            System.out.println("Skriv ja eller nej.");
        }
    }

    // Läser in ett namn och fortsätter fråga tills namnet är giltigt.
    public static String readValidName(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            // Ser till att namnet inte är tomt.
            if (input.isBlank()) {
                System.out.println("Namnet får inte vara tomt.");
                continue;
            }

            // Kollar att namnet bara innehåller bokstäver och mellanslag.
            if (!input.matches("[a-zA-ZåäöÅÄÖ ]+")) {
                System.out.println("Namnet får bara innehålla bokstäver och mellanslag.");
                continue;
            }

            return input;
        }
    }
}
