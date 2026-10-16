package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

// Ärver gemensamma egenskaper från Attack och ansvarar för skadeberäkningen.
public class DamageAttack extends Attack {
    private static final Random RANDOM = new Random();

    // Jackson använder konstruktorn för att läsa in attacker från JSON.
    @JsonCreator
    public DamageAttack(
            @JsonProperty("name") String name,
            @JsonProperty("baseDamage") int baseDamage,
            @JsonProperty("accuracy") int accuracy,
            @JsonProperty("type") Type type
    ) {
        // super anropar basklassens konstruktor för validering och initiering.
        super(
                name,
                baseDamage,
                accuracy,
                type
        );
    }

    // Utför en attack och returnerar ett AttackResult med resultatet.
    public AttackResult performAttack(Pokemon attacker, Pokemon defender) {

        // Slumpar 1–100 för att avgöra om attacken träffar.
        int roll = RANDOM.nextInt(100) + 1;

        // Om det slumpade talet är högre än träffsäkerheten missar attacken.
        // Ingen skada delas ut och resultatet visar att attacken missade.
        if (roll > getAccuracy()) {
            return new AttackResult(
                    false,
                    0,
                    false,
                    1.0);
        }

        // TypeEffectiveness beräknar typfördelen separat.
        double effectiveness = TypeEffectiveness.calculate(
                getType(),
                defender.getType()
        );

        // Slumpfaktorn varierar skadan mellan 85 % och 100 %.
        double randomFactor = 0.85 + RANDOM.nextDouble() * 0.15;

        // Beräknar skadan och säkerställer minst 1 skada vid träff.
        int damage = Math.max(1, (int) (
                getBaseDamage() * effectiveness * randomFactor
        ));

        // Ger 1/16 chans för en kritisk träff.
        boolean criticalHit = RANDOM.nextInt(16) == 0;

        // En kritisk träff fördubblar den beräknade skadan.
        if (criticalHit) {
            damage *= 2;
        }

        // Inkapsling: Pokemon ansvarar själv för att minska sitt HP.
        defender.takeDamage(damage);

        // Returnerar skada, träffresultat, kritisk träff och typfördel.
        return new AttackResult(
                true,
                damage,
                criticalHit,
                effectiveness
        );
    }

    // Implementerar den abstrakta metoden från Attack (polymorfism).
    @Override
    public AttackResult execute(Pokemon attacker, Pokemon defender) {
        return performAttack(attacker, defender);
    }
}
