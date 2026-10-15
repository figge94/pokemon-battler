package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

public class DamageAttack extends Attack {
    private static final Random RANDOM = new Random();

    @JsonCreator
    public DamageAttack(
            @JsonProperty("name") String name,
            @JsonProperty("baseDamage") int baseDamage,
            @JsonProperty("accuracy") int accuracy,
            @JsonProperty("type") Type type
    ) {
        super(
                name,
                baseDamage,
                accuracy,
                type
        );
    }

    public AttackResult performAttack(Pokemon attacker, Pokemon defender) {
        int roll = RANDOM.nextInt(100) + 1;

        // Kontrollerar om attacken träffar.
        if (roll > getAccuracy()) {
            return new AttackResult(false, 0, false, 1.0);
        }

        double effectiveness = TypeEffectiveness.calculate(
                getType(),
                defender.getType()
        );

        double randomFactor = 0.85 + RANDOM.nextDouble() * 0.15;

        int damage = (int) (
                getBaseDamage() * effectiveness * randomFactor
        );

        // Avgör om attacken blir kritisk.
        boolean criticalHit = RANDOM.nextInt(16) == 0;

        if (criticalHit) {
            damage *= 2;
        }

        defender.takeDamage(damage);

        return new AttackResult(true, damage, criticalHit, effectiveness);
    }

    @Override
    public AttackResult execute(Pokemon attacker, Pokemon defender) {

        // Utför attacken och returnerar resultatet.
        return performAttack(attacker, defender);
    }
}