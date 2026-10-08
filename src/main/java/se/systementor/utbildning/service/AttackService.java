package se.systementor.utbildning.service;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;

public class AttackService {
    public static void addAttackToPokemon(
            Pokemon pokemon,
            Attack attack
    ) {
        pokemon.addAttack(attack);
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
            case FIRE -> new Attack("Flame Wheel", 60, 100, Type.FIRE);
            case WATER -> new Attack("Water Gun", 40, 100, Type.WATER);
            case GRASS -> new Attack("Razor Leaf", 55, 95, Type.GRASS);
            case ELECTRIC -> new Attack("Thunder Shock", 40, 100, Type.ELECTRIC);
            case NORMAL -> new Attack("Scratch", 40, 100, Type.NORMAL);
            case ICE -> new Attack("Ice Shard", 40, 100, Type.ICE);
            case PSYCHIC -> new Attack("Psychic", 90, 100, Type.PSYCHIC);
            case BUG -> new Attack("Skitter Smack", 70, 90, Type.BUG);
            case FAIRY -> new Attack("Fairy Wind", 40, 100, Type.FAIRY);
        };
    }
}
