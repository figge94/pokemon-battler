package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;

// Formaterar en Pokemons egenskaper och attacker till text.
public final class PokemonFormatter {

    // Privat konstruktor eftersom klassen bara innehåller statiska metoder.
    private PokemonFormatter() {
    }

    // Tar emot en Pokemon och returnerar en formaterad text med Pokemons egenskaper och tillhörande attacker.
    public static String format(Pokemon pokemon) {

        // StringBuilder bygger upp texten stegvis.
        // String.format placerar värden i en bestämd struktur.
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

        // Anpassar attackernas indrag efter antalet siffror i ID:t.
        int numberOfCharactersInId =
                String.valueOf(pokemon.getId()).length();

        // Lägger till en formaterad textrad för varje attack.
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

        // Omvandlar StringBuilder till en String.
        return pokemonDetails.toString();
    }
}