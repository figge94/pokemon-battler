
package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;

public class PokemonFormatter {

    public static String format(Pokemon pokemon) {
        StringBuilder pokemonDetails = new StringBuilder(
                String.format(
                        "%d. Namn: %s | Typ: %s | HP: %d/%d | Snabbhet: %d%n ",
                        pokemon.getId(),
                        pokemon.getName(),
                        pokemon.getType().getLabel(),
                        pokemon.getCurrentHp(),
                        pokemon.getMaxHp(),
                        pokemon.getSpeed()
                )
        );

        // Anpassar indraget efter hur många siffror ID:t har.
        int numberOfCharactersInId =
                String.valueOf(pokemon.getId()).length();

        for (Attack attack : pokemon.getAttacks()) {
            pokemonDetails.append(
                    String.format(
                            "%s  Attack: %s | Skada: %d | Träffsäkerhet: %d | Typ: %s%n",
                            " ".repeat(numberOfCharactersInId),
                            attack.getName(),
                            attack.getBaseDamage(),
                            attack.getAccuracy(),
                            attack.getType().getLabel()
                    )
            );
        }

        return pokemonDetails.toString();
    }
}
