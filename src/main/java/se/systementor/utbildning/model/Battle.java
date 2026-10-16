package se.systementor.utbildning.model;

// Representerar en strid och hanterar deltagare, turordning och resultat.
public class Battle {
    // final gör att deltagarna inte kan bytas ut under striden.
    private final Pokemon playerPokemon;
    private final Pokemon wildPokemon;

    // Håller reda på om spelaren har flytt.
    private boolean fled = false;

    // Skapar en strid med två olika Pokemon.
    public Battle(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        if (playerPokemon == null || wildPokemon == null) {
            throw new IllegalArgumentException(
                    "En strid måste ha två Pokemon."
            );
        }
        // equals jämför Pokemonens ID så att den inte möter sig själv.
        if (playerPokemon.equals(wildPokemon)) {
            throw new IllegalArgumentException(
                    "En Pokemon kan inte strida mot sig själv."
            );
        }

        this.playerPokemon = playerPokemon;
        this.wildPokemon = wildPokemon;
    }

    public Pokemon getPlayerPokemon() {
        return playerPokemon;
    }

    public Pokemon getWildPokemon() {
        return wildPokemon;
    }

    // Avgör vem som börjar utifrån snabbhet.
    public Pokemon getFastestPokemon() {
        // Vid lika snabbhet avgör slumpen vem som börjar.
        if (playerPokemon.getSpeed() == wildPokemon.getSpeed()) {
            return Math.random() < 0.5
                    ? playerPokemon
                    : wildPokemon;
        }

        if (playerPokemon.getSpeed() > wildPokemon.getSpeed()) {
            return playerPokemon;
        }

        return wildPokemon;
    }

    // Bestämmer turordningen för båda Pokemon i striden.
    public Pokemon[] getTurnOrder() {

        // Hämtar den snabbaste Pokemon, som får börja.
        Pokemon first = getFastestPokemon();

        // Kontrollerar om spelarens Pokemon börjar.
        // Om den gör det blir den vilda Pokemon nummer två, annars tvärtom.
        Pokemon second = first == playerPokemon
                ? wildPokemon
                : playerPokemon;

        // Returnerar en array med Pokemon i rätt turordning.
        return new Pokemon[]{first, second};
    }

    // Striden avslutas vid flykt eller när någon Pokemon är utslagen.
    public boolean isOver() {
        return fled
                || playerPokemon.isKnockedOut()
                || wildPokemon.isKnockedOut();
    }

    // Spelaren vinner om motståndaren är utslagen och den egna Pokemonen lever.
    public boolean playerWon() {
        return wildPokemon.isKnockedOut()
                && !playerPokemon.isKnockedOut();
    }

    // Registrerar att spelaren flyr från striden.
    public void flee() {

        // Förhindrar flykt från en redan avslutad strid.
        if (isOver()) {
            throw new IllegalStateException(
                    "Det går inte att fly från en avslutad strid."
            );
        }

        fled = true;
    }

    // Kontrollerar om spelaren har flytt.
    public boolean hasFled() {
        return fled;
    }
}
