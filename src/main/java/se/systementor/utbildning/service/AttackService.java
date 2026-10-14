package se.systementor.utbildning.service;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.DamageAttack;
import se.systementor.utbildning.model.Type;

public class AttackService {

    public static Attack createAttack(
            String name,
            int damage,
            int accuracy,
            Type type
            ) {
        return new DamageAttack(
                name,
                damage,
                accuracy,
                type
        );
    }

    // Skapar en standardattack beroende på vald Pokemon-typ.
    public static Attack createDefaultAttack(Type type) {
        return switch (type) {
            case FIRE -> new DamageAttack("Flame Wheel", 60, 100, type);
            case WATER -> new DamageAttack("Water Gun", 40, 100, type);
            case GRASS -> new DamageAttack("Razor Leaf", 55, 95, type);
            case ELECTRIC -> new DamageAttack("Thunder Shock", 40, 100, type);
            case NORMAL -> new DamageAttack("Scratch", 40, 100, type);
            case ICE -> new DamageAttack("Ice Shard", 40, 100, type);
            case PSYCHIC -> new DamageAttack("Psychic", 90, 100, type);
            case BUG -> new DamageAttack("Skitter Smack", 70, 90, type);
            case FAIRY -> new DamageAttack("Fairy Wind", 40, 100, type);
        };
    }
}
