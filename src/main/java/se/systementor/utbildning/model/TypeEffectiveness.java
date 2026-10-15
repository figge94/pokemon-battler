package se.systementor.utbildning.model;

public final class TypeEffectiveness {

    private TypeEffectiveness() {
        // Förhindrar att klassen skapas som ett objekt.
    }

    // Räknar ut hur effektiv attacktypen är mot försvararens typ.
    public static double calculate(
            Type attackType,
            Type defenderType
    ) {
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

            case ELECTRIC -> defenderType == Type.WATER ? 2.0 : 1.0;

            default -> 1.0;
        };
    }
}
