package se.systementor.utbildning.model;

public class Battle {
    private final Pokemon playerPokemon;
    private final Pokemon wildPokemon;
    private boolean fled = false;

    // Skapar en ny strid mellan spelarens Pokemon och en vild Pokemon.
    public Battle(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        if (playerPokemon == null || wildPokemon == null) {
            throw new IllegalArgumentException(
                    "En strid måste ha två Pokemon."
            );
        }

        if (playerPokemon.equals(wildPokemon)) {
            throw new IllegalArgumentException(
                    "En Pokemon kan inte strida mot sig själv."
            );
        }

        this.playerPokemon = playerPokemon;
        this.wildPokemon = wildPokemon;
    }

    // Hämtar spelarens Pokemon.
    public Pokemon getPlayerPokemon() {
        return playerPokemon;
    }

    // Hämtar en vild Pokemon.
    public  Pokemon getWildPokemon() {
        return wildPokemon;
    }

    // Hämtar den snabbaste pokemon
    public Pokemon getFastestPokemon() {
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

    public Pokemon[] getTurnOrder() {
        Pokemon first = getFastestPokemon();
        Pokemon second = first == playerPokemon
                ? wildPokemon
                : playerPokemon;

        return new Pokemon[]{first, second};
    }

    // Kontrollerar om striden är slut.
    public boolean isOver() {
        return fled
                || playerPokemon.isKnockedOut()
                || wildPokemon.isKnockedOut();
    }

    // Kontrollerar om spelaren vann striden.
    public boolean playerWon() {
        return wildPokemon.isKnockedOut()
                && !playerPokemon.isKnockedOut();
    }

    // Registrerar att spelaren flyr.
    public void flee() {
        if (isOver()) {
            throw new IllegalStateException(
                    "Det går inte att fly från en avslutad strid."
            );
        }

        fled = true;
    }

    public boolean hasFled() {
        return fled;
    }
}
