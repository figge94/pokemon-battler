package se.systementor.utbildning.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TypeEffectivenessTest {

    @Test
    void shouldDoubleDamageAgainstWeakType() {
        // Elektriska attacker är supereffektiva mot vatten.
        double result = TypeEffectiveness.calculate(
                Type.ELECTRIC,
                Type.WATER
        );

        assertEquals(2.0, result);
    }

    @Test
    void shouldHalveDamageAgainstResistantType() {
        // Eldattacker gör halv skada mot vatten.
        double result = TypeEffectiveness.calculate(
                Type.FIRE,
                Type.WATER
        );

        assertEquals(0.5, result);
    }

    @Test
    void shouldReturnNormalDamageForNeutralType() {
        // Normalattacker gör vanlig skada mot vatten.
        double result = TypeEffectiveness.calculate(
                Type.NORMAL,
                Type.WATER
        );

        assertEquals(1.0, result);
    }

    @Test
    void shouldRejectNullTypes() {
        // Typberäkningen ska inte acceptera null.
        assertThrows(IllegalArgumentException.class, () ->
                TypeEffectiveness.calculate(null, Type.WATER)
        );

        assertThrows(IllegalArgumentException.class, () ->
                TypeEffectiveness.calculate(Type.FIRE, null)
        );
    }
}
