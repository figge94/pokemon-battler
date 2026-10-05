package se.systementor.utbildning;

public class Menu {
    // Visar programmets huvudmeny.
    public static void showMainMenu() {
        System.out.println("\n=== POKEDEX ===");
        System.out.println("1: Visa alla Pokemon");
        System.out.println("2: Spela");
        System.out.println("3: Sök Pokemon");
        System.out.println("4: Lägg till ny Pokemon");
        System.out.println("5: Redigera Pokemon");
        System.out.println("6: Ta bort Pokemon");
        System.out.println("7: Spara till fil");
        System.out.println("8: Ladda från fil");
        System.out.println("9: Återställ till seedad data");
        System.out.println("10: Avsluta");
    }
    // Visar menyn för att redigera en Pokemon.
    public static void showEditMenu() {
        System.out.println("Vad vill du redigera?");
        System.out.println("1: Namn");
        System.out.println("2: Typ");
        System.out.println("3: Nuvarande HP");
        System.out.println("4: Max HP");
        System.out.println("5: Lägg till en ny attack");
        System.out.println("6: Ta bort en attack");
        System.out.println("7: Gå tillbaka");
    }

    public static void showPlayMenu(){
        System.out.println("1: Strid");
        System.out.println("2: Resultat");
        System.out.println("3: Gå tillbaka");
    }
}
