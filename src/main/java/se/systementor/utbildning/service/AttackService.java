package se.systementor.utbildning.service;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.DamageAttack;
import se.systementor.utbildning.model.Type;

// Ansvarar för att skapa attacker så att andra klasser
// inte behöver känna till den konkreta klassen DamageAttack.
public class AttackService {

    // Returnerar Attack för att möjliggöra olika attacktyper (polymorfism).
    public static Attack createAttack(
            String name,
            int damage,
            int accuracy,
            Type type
    ) {
        // Attack-konstruktorn ansvarar för valideringen.
        return new DamageAttack(
                name,
                damage,
                accuracy,
                type
        );
    }

    // Skapar en standardattack utifrån Pokemons typ.
    public static Attack createDefaultAttack(Type type) {

        // Switch-uttrycket returnerar en attack för den valda typen.
        // Återanvänder createAttack i stället för att skapa objekten direkt.
        return switch (type) {
            case FIRE -> createAttack("Flame Wheel", 60, 100, type);
            case WATER -> createAttack("Water Gun", 40, 100, type);
            case GRASS -> createAttack("Razor Leaf", 55, 95, type);
            case ELECTRIC -> createAttack("Thunder Shock", 40, 100, type);
            case NORMAL -> createAttack("Scratch", 40, 100, type);
            case ICE -> createAttack("Ice Shard", 40, 100, type);
            case PSYCHIC -> createAttack("Psychic", 90, 100, type);
            case BUG -> createAttack("Skitter Smack", 70, 90, type);
            case FAIRY -> createAttack("Fairy Wind", 40, 100, type);
        };
    }
}