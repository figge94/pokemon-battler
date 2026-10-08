package se.systementor.utbildning.service;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.DamageAttack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

public class AttackService {
    public static void addAttackToPokemon(
            Pokemon pokemon,
            Attack attack
    ) {
        pokemon.addAttack(attack);
    }

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

    // Tar bort en attack från vald Pokemon.
    public static void removeAttackFromPokemon(
            Pokemon pokemon,
            Attack attack
    ) {
        pokemon.removeAttack(attack);
    }

    // Skapar en standardattack beroende på vald Pokemon-typ.
    public static Attack createDefaultAttack(Type type) {
        return switch (type) {
            case FIRE ->     new DamageAttack("Flame Wheel", 60, 100, Type.FIRE);
            case WATER ->    new DamageAttack("Water Gun", 40, 100, Type.WATER);
            case GRASS ->    new DamageAttack("Razor Leaf", 55, 95, Type.GRASS);
            case ELECTRIC -> new DamageAttack("Thunder Shock", 40, 100, Type.ELECTRIC);
            case NORMAL ->   new DamageAttack("Scratch", 40, 100, Type.NORMAL);
            case ICE ->      new DamageAttack("Ice Shard", 40, 100, Type.ICE);
            case PSYCHIC ->  new DamageAttack("Psychic", 90, 100, Type.PSYCHIC);
            case BUG ->      new DamageAttack("Skitter Smack", 70, 90, Type.BUG);
            case FAIRY ->    new DamageAttack("Fairy Wind", 40, 100, Type.FAIRY);
        };
    }
}
