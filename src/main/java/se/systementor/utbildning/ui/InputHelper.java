package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Type;

import java.util.Scanner;

// Hjälpklass som ansvarar för att läsa och validera användarinmatning.
public final class InputHelper {

    // Privat konstruktor eftersom alla metoder är statiska.
    private InputHelper() {
    }

    // Läser in ett heltal inom ett angivet intervall.
    // Fortsätter fråga tills användaren anger ett giltigt värde.
    public static int readIntInRange(
            Scanner scanner,
            String prompt,
            int min,
            int max
    ) {
        // Loopen fortsätter tills ett giltigt tal kan returneras.
        while (true) {

            // Visar frågan och läser in användarens svar som text.
            System.out.print(prompt);
            String input = scanner.nextLine();

            try {
                // Försöker omvandla texten till ett heltal.
                // Ogiltig text ger NumberFormatException.
                int value = Integer.parseInt(input);

                // Kontrollerar att talet ligger inom det tillåtna intervallet.
                // continue gör att användaren får försöka igen.
                if (value < min || value > max) {
                    System.out.println(
                            "Värdet måste vara mellan " + min + " och " + max + "."
                    );
                    continue;
                }

                // Returnerar talet när både format och intervall är giltiga.
                return value;

            } catch (NumberFormatException e) {

                // Hanterar text som inte kan omvandlas till ett heltal.
                // Programmet fortsätter i stället för att krascha.
                System.out.println("Du måste skriva ett heltal.");
            }
        }
    }

    // Låter användaren välja en Pokemon-typ från Type-enum.
    public static Type chooseType(Scanner scanner) {

        // Hämtar alla tillgängliga typer från enum-klassen.
        // Därmed behöver typerna inte skrivas in manuellt i menyn.
        Type[] types = Type.values();

        System.out.println("Välj typ:");

        // Skriver ut alla typer med ett nummer framför varje alternativ.
        // Nummereringen börjar på 1 för att vara tydlig för användaren.
        for (int i = 0; i < types.length; i++) {
            // Visar typens svenska namn i stället för enum-konstanten.
            System.out.println((i + 1) + ": " + types[i].getLabel());
        }

        // Återanvänder metoden för heltalsinmatning.
        // Användaren kan bara välja ett nummer som finns i listan.
        int choice = readIntInRange(
                scanner,
                "Val: ",
                1,
                types.length
        );

        // Omvandlar användarens 1-baserade val till arrayens 0-baserade index.
        // Returnerar motsvarande värde från Type-enum.
        return types[choice - 1];
    }

    // Läser in ett ja- eller nej-svar och returnerar true eller false.
    public static boolean readYesNo(Scanner scanner, String prompt) {

        // Fortsätter fråga tills användaren skriver ett giltigt svar.
        while (true) {

            // Visar frågan och läser användarens svar.
            System.out.print(prompt);
            String input = scanner.nextLine();

            // equalsIgnoreCase gör att både ja och JA godkänns.
            if (input.equalsIgnoreCase("ja")) {
                return true;
            }

            // Returnerar false om användaren svarar nej.
            if (input.equalsIgnoreCase("nej")) {
                return false;
            }

            // Visar felmeddelande om svaret inte är ja eller nej.
            // Därefter upprepas loopen automatiskt.
            System.out.println("Skriv ja eller nej.");
        }
    }

    // Läser in ett namn och kontrollerar att det följer programmets regler.
    public static String readValidName(Scanner scanner, String prompt) {

        // Fortsätter fråga tills ett giltigt namn har angetts.
        while (true) {

            // Läser in namnet och tar bort mellanslag i början och slutet.
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            // Kontrollerar att namnet inte är tomt.
            // continue låter användaren försöka igen.
            if (input.isBlank()) {
                System.out.println("Namnet får inte vara tomt.");
                continue;
            }

            // Kontrollerar att namnet bara innehåller bokstäver och mellanslag.
            // Samma namnregel används i Pokemon och Attack.
            if (!input.matches("[a-zA-ZåäöÅÄÖ ]+")) {
                System.out.println(
                        "Namnet får bara innehålla bokstäver och mellanslag."
                );
                continue;
            }

            return input;
        }
    }
}