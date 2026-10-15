package se.systementor.utbildning.model;

// Hjälpklass som ansvarar för att beräkna typfördelar i strider.
// Genom att samla reglerna här slipper DamageAttack
// innehålla villkor för alla olika Pokemon-typer.
public final class TypeEffectiveness {

    // Privat konstruktor förhindrar att objekt av klassen skapas.
    // Klassen innehåller bara en statisk metod och behöver inget eget tillstånd.
    private TypeEffectiveness() {
    }

    // Beräknar hur effektiv en attacktyp är mot försvararens typ.
    // Returnerar en multiplikator som används vid skadeberäkningen.
    public static double calculate(
            Type attackType,
            Type defenderType
    ) {

        // Kontrollerar att båda typerna finns innan beräkningen.
        // Detta förhindrar att ogiltiga värden används i switch-uttrycket.
        if (attackType == null || defenderType == null) {
            throw new IllegalArgumentException(
                    "Attacktyp och försvarartyp får inte vara null."
            );
        }

        // Väljer rätt skadefaktor utifrån attackens och försvararens typ.
        // 2.0 betyder dubbel skada, 0.5 betyder halv skada
        // och 1.0 betyder normal skada.
        return switch (attackType) {

            // Eld är starkt mot gräs men svagt mot vatten.
            // Övriga kombinationer ger normal skada.
            case FIRE -> switch (defenderType) {
                case GRASS -> 2.0;
                case WATER -> 0.5;
                default -> 1.0;
            };

            // Vatten är starkt mot eld men svagt mot gräs.
            case WATER -> switch (defenderType) {
                case FIRE -> 2.0;
                case GRASS -> 0.5;
                default -> 1.0;
            };

            // Gräs är starkt mot vatten men svagt mot eld.
            case GRASS -> switch (defenderType) {
                case WATER -> 2.0;
                case FIRE -> 0.5;
                default -> 1.0;
            };

            // Elektriska attacker gör dubbel skada mot vatten.
            // Mot övriga typer används normal skada.
            case ELECTRIC -> defenderType == Type.WATER ? 2.0 : 1.0;

            // Attacktyper utan särskilda regler gör normal skada.
            default -> 1.0;
        };
    }
}
