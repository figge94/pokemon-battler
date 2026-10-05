package se.systementor.utbildning;

public enum Type {
    FIRE("Eld"),
    WATER("Vatten"),
    GRASS("Gräs"),
    ELECTRIC("Elektrisk"),
    BUG("Insekt"),
    ICE("Is"),
    PSYCHIC("Psykisk"),
    FAIRY("Fe"),
    NORMAL("Normal");

    private final String label;

    Type(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}