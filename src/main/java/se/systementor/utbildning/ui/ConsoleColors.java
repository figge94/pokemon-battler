package se.systementor.utbildning.ui;

// Hjälpklass som samlar färgkoder för konsolens utskrifter.
// Genom att ha färgerna på ett ställe slipper andra klasser
// skriva samma färgkoder flera gånger.
public final class ConsoleColors {

    // ANSI-koder som används för att ändra textfärgen i konsolen.
    // public gör dem tillgängliga för andra klasser.
    // static gör att de kan användas utan att skapa ett objekt.
    // final innebär att värdena inte kan ändras.
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    // Privat konstruktor eftersom klassen bara innehåller konstanter.
    // Det finns därför ingen anledning att skapa objekt av ConsoleColors.
    private ConsoleColors() {
    }
}