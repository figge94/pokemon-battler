package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;
import se.systementor.utbildning.service.BattleService;
import se.systementor.utbildning.ui.BattleMenu;

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

    @Override
    public void execute(
            Pokemon attacker,
            Pokemon defender
    ) {
        int roll = RANDOM.nextInt(100) + 1;

        if (roll > getAccuracy()) {
            BattleMenu.showMiss(attacker, this);
            return;
        }

        double effectiveness = BattleService.getTypeEffectiveness(
                getType(),
                defender.getType()
        );

        // Slumpfaktor mellan 85 % och 100 %.
        double randomFactor = 0.85 + RANDOM.nextDouble() * 0.15;

        int damage = (int) (
                getBaseDamage() * effectiveness * randomFactor
        );

        // 1 av 16 chans att göra dubbel skada.
        boolean criticalHit = RANDOM.nextInt(16) == 0;

        if (criticalHit) {
            damage *= 2;
            BattleMenu.showCriticalHit();
        }

        defender.takeDamage(damage);

        BattleMenu.showAttackResult(
                attacker,
                defender,
                this,
                damage,
                effectiveness
        );
    }
}