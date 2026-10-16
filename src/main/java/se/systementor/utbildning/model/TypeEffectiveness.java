package se.systementor.utbildning.model;

// Beräknar typfördelar separat från stridslogiken i DamageAttack.
public final class TypeEffectiveness {

    // Privat konstruktor eftersom klassen bara innehåller statiska metoder.
    private TypeEffectiveness() {
    }

    // Returnerar en multiplikator utifrån attackens och försvararens typ.
    public static double calculate(
            Type attackType,
            Type defenderType
    ) {

        if (attackType == null || defenderType == null) {
            throw new IllegalArgumentException(
                    "Attacktyp och försvarartyp får inte vara null."
            );
        }

        // Switch-uttrycket returnerar 2.0 för dubbel skada,
        // 0.5 för halv skada och 1.0 för normal skada.
        return switch (attackType) {

            case FIRE -> switch (defenderType) {
                case GRASS -> 2.0;
                case WATER -> 0.5;
                default -> 1.0;
            };


            case WATER -> switch (defenderType) {
                case FIRE -> 2.0;
                case GRASS -> 0.5;
                default -> 1.0;
            };

            case GRASS -> switch (defenderType) {
                case WATER -> 2.0;
                case FIRE -> 0.5;
                default -> 1.0;
            };

            // Villkorsoperatorn ger dubbel skada mot vatten
            case ELECTRIC -> defenderType == Type.WATER ? 2.0 : 1.0;

            // Övriga attacktyper gör normal skada.
            default -> 1.0;
        };
    }
}
