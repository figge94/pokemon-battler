
package se.systementor.utbildning.model;

// Enum begränsar Pokemon till de fördefinierade typerna.
public enum Type {

    // Varje typ har en svensk benämning som visas för användaren.
    FIRE("Eld"),
    WATER("Vatten"),
    GRASS("Gräs"),
    ELECTRIC("Elektrisk"),
    BUG("Insekt"),
    ICE("Is"),
    PSYCHIC("Psykisk"),
    FAIRY("Fe"),
    NORMAL("Normal");

    // final gör att benämningen inte kan ändras efter skapandet.
    private final String label;

    // Konstruktorn anropas automatiskt när enum-värdena skapas.
    Type(String label) {
        this.label = label;
    }

    // Returnerar typens svenska benämning.
    public String getLabel() {
        return label;
    }
}
