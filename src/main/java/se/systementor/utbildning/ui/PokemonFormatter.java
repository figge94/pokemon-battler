package se.systementor.utbildning.ui;

import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;

// Ansvarar för att formatera information om en Pokemon till text.
// Genom att samla formateringen här slipper andra UI-klasser
// bygga samma text på flera olika ställen.
public final class PokemonFormatter {

    // Privat konstruktor eftersom klassen bara innehåller
    // statiska metoder och inte ska skapas som objekt.
    private PokemonFormatter() {
    }

    // Tar emot en Pokemon och returnerar en formaterad text
    // med Pokemonens egenskaper och tillhörande attacker.
    public static String format(Pokemon pokemon) {

        // StringBuilder används för att bygga upp texten stegvis.
        // String.format gör att Pokemonens värden kan placeras
        // i en gemensam textrad med samma struktur.
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

        // Räknar ut hur många siffror Pokemonens ID innehåller.
        // Detta används för att anpassa attackernas indrag
        // så att utskriften blir tydlig även för ID med flera siffror.
        int numberOfCharactersInId =
                String.valueOf(pokemon.getId()).length();

        // Går igenom alla attacker som tillhör Pokemonen.
        // Attackerna hämtas genom getAttacks() för att
        // inte komma åt den privata listan direkt.
        for (Attack attack : pokemon.getAttacks()) {

            // Lägger till en ny textrad för varje attack.
            // String.repeat skapar mellanslag utifrån ID:ts längd.
            // Attackens egenskaper hämtas genom dess getters.
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

        // Omvandlar den färdiga texten från StringBuilder
        // till en String som kan skrivas ut av PokemonMenu.
        return pokemonDetails.toString();
    }
}