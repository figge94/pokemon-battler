package se.systementor.utbildning.model;

public class Battle {
    private final Pokemon playerPokemon;
    private final Pokemon wildPokemon;

    // Skapar en ny strid mellan spelarens Pokemon och en vild Pokemon.
    public Battle(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        this.playerPokemon = playerPokemon;
        this.wildPokemon = wildPokemon;
    }

    // Hämtar spelarens Pokemon.
    public Pokemon getPlayerPokemon() {
        return playerPokemon;
    }

    // Hämtar en vild Pokemon
    public  Pokemon getWildPokemon() {
        return  wildPokemon;
    }

    // Kontrollerar om striden är slut.
    public boolean isOver() {
        return playerPokemon.isKnockedOut()
                || wildPokemon.isKnockedOut();
    }

    // Kontrollerar om spelaren vann striden.
    public boolean playerWon() {
        return wildPokemon.isKnockedOut();
    }

    // Returnerar vinnaren när striden är slut.
    public Pokemon getWinner() {
        if (!isOver()) {
            return null;
        }

        if (playerWon()) {
            return playerPokemon;
        }

        return wildPokemon;
    }
}
