
package se.systementor.utbildning.model;

// Enum som definierar vilka Pokemon-typer som finns i spelet.
// Genom att använda enum kan en Pokemon bara ha en av
// de fördefinierade typerna, vilket förhindrar ogiltiga typvärden.
public enum Type {

    // Varje typ har ett konstant namn som används i programmet
    // och en svensk benämning som kan visas för användaren.
    FIRE("Eld"),
    WATER("Vatten"),
    GRASS("Gräs"),
    ELECTRIC("Elektrisk"),
    BUG("Insekt"),
    ICE("Is"),
    PSYCHIC("Psykisk"),
    FAIRY("Fe"),
    NORMAL("Normal");

    // Lagrar den svenska benämningen för varje Pokemon-typ.
    // final innebär att benämningen inte kan ändras efter skapandet.
    private final String label;

    // Konstruktorn används automatiskt när enum-värdena skapas.
    // Varje typ får sin svenska benämning via parametern label.
    Type(String label) {
        this.label = label;
    }

    // Returnerar den svenska benämningen på typen.
    // Andra klasser kan läsa värdet utan att få direkt
    // åtkomst till det privata fältet.
    public String getLabel() {
        return label;
    }
}
