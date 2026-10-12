package se.systementor.utbildning.ui;

public class Menu {

    // Visar programmets huvudmeny.
    public static void showMainMenu() {
        ConsoleUI.printMenu(
                "POKEMON BATTLER",
                ConsoleColors.CYAN,
                "1. Visa alla Pokémon",
                "2. Spela",
                "3. Sök Pokémon",
                "4. Lägg till ny Pokémon",
                "5. Redigera Pokémon",
                "6. Ta bort Pokémon",
                "7. Spara till fil",
                "8. Ladda från fil",
                "9. Återställ till seedad data",
                "10. Avsluta"
        );
    }

    // Visar menyn för att redigera en Pokemon.
    public static void showEditMenu() {
        ConsoleUI.printMenu(
                "REDIGERA POKÉMON",
                ConsoleColors.YELLOW,
                "1. Ändra namn",
                "2. Ändra typ",
                "3. Ändra nuvarande HP",
                "4. Ändra max HP",
                "5. Lägg till attack",
                "6. Ta bort attack",
                "7. Gå tillbaka"
        );
    }

    // Visar menyn för att spela.
    public static void showPlayMenu() {
        ConsoleUI.printMenu(
                "STRID",
                ConsoleColors.GREEN,
                "1. Strid",
                "2. Resultat",
                "3. Gå tillbaka"
        );
    }
}