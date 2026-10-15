package se.systementor.utbildning.service;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.DamageAttack;
import se.systementor.utbildning.model.Type;

// Ansvarar för att skapa attacker med rätt egenskaper.
// Genom att samla skapandet här slipper andra klasser
// själva bestämma vilken konkret attackklass som ska användas.
public class AttackService {

    // Skapar en ny attack utifrån användarens valda egenskaper.
    // Returnerar basklassen Attack för att andra klasser ska
    // kunna arbeta med olika attacktyper genom polymorfism.
    public static Attack createAttack(
            String name,
            int damage,
            int accuracy,
            Type type
    ) {
        // Skapar en DamageAttack med de angivna egenskaperna.
        // Konstruktorn i Attack ansvarar för att validera värdena.
        return new DamageAttack(
                name,
                damage,
                accuracy,
                type
        );
    }

    // Skapar en standardattack utifrån Pokemonens typ.
    // Används när en ny Pokemon skapas och behöver sin första attack.
    public static Attack createDefaultAttack(Type type) {

        // Switch väljer vilken attack som ska skapas beroende på typen.
        // Varje alternativ anger namn, grundskada och träffsäkerhet.
        // Samma typ skickas vidare till attackens konstruktor.
        return switch (type) {

            // Standardattack för Pokemon av eldtyp.
            case FIRE -> new DamageAttack("Flame Wheel", 60, 100, type);

            // Standardattack för Pokemon av vattentyp.
            case WATER -> new DamageAttack("Water Gun", 40, 100, type);

            // Standardattack för Pokemon av grästyp.
            case GRASS -> new DamageAttack("Razor Leaf", 55, 95, type);

            // Standardattack för Pokemon av elektrisk typ.
            case ELECTRIC -> new DamageAttack("Thunder Shock", 40, 100, type);

            // Standardattack för Pokemon av normaltyp.
            case NORMAL -> new DamageAttack("Scratch", 40, 100, type);

            // Standardattack för Pokemon av istyp.
            case ICE -> new DamageAttack("Ice Shard", 40, 100, type);

            // Standardattack för Pokemon av psykisk typ.
            case PSYCHIC -> new DamageAttack("Psychic", 90, 100, type);

            // Standardattack för Pokemon av insektstyp.
            case BUG -> new DamageAttack("Skitter Smack", 70, 90, type);

            // Standardattack för Pokemon av fetyp.
            case FAIRY -> new DamageAttack("Fairy Wind", 40, 100, type);
        };
    }
}
