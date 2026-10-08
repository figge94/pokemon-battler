package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DamageAttack extends Attack {

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
        defender.takeDamage(getBaseDamage());
    }
}