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
                "REDIGERA POKEMON",
                ConsoleColors.YELLOW,
                "1. Ändra namn",
                "2. Ändra typ",
                "3. Ändra nuvarande HP",
                "4. Ändra max HP",
                "5. Ändra snabbheten",
                "6. Lägg till attack",
                "7. Ta bort attack",
                "8. Gå tillbaka till huvudmenyn"
        );
    }

    // Visar menyn för att spela.
    public static void showPlayMenu() {
        ConsoleUI.printMenu(
                "STRID",
                ConsoleColors.GREEN,
                "1. Starta strid",
                "2. Total statistik",
                "3. Statistik per Pokemon",
                "4. Mest använda Pokemon",
                "5. Mest använda attack",
                "6. Gå tillbaka till huvudmenyn"
        );
    }
}