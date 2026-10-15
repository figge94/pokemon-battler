package se.systementor.utbildning.model;

// Representerar en strid mellan spelarens Pokemon och en vild Pokemon.
// Klassen ansvarar för stridens tillstånd, turordning och resultat.
public class Battle {
    // Stridens deltagare kan inte bytas ut efter att striden skapats.
    // fled håller reda på om spelaren har avslutat striden genom flykt.
    private final Pokemon playerPokemon;
    private final Pokemon wildPokemon;
    private boolean fled = false;

    // Skapar en strid och säkerställer att båda deltagarna är giltiga.
    public Battle(
            Pokemon playerPokemon,
            Pokemon wildPokemon
    ) {
        // Förhindrar att en strid startas utan två Pokemon.
        if (playerPokemon == null || wildPokemon == null) {
            throw new IllegalArgumentException(
                    "En strid måste ha två Pokemon."
            );
        }
        // Förhindrar att samma Pokemon möter sig själv.
        if (playerPokemon.equals(wildPokemon)) {
            throw new IllegalArgumentException(
                    "En Pokemon kan inte strida mot sig själv."
            );
        }

        // Sparar deltagarna när valideringen är godkänd.
        this.playerPokemon = playerPokemon;
        this.wildPokemon = wildPokemon;
    }

    // Returnerar spelarens Pokemon som deltar i striden.
    public Pokemon getPlayerPokemon() {
        return playerPokemon;
    }

    // Returnerar den vilda Pokemon som är motståndare.
    public Pokemon getWildPokemon() {
        return wildPokemon;
    }

    // Avgör vilken Pokemon som ska attackera först utifrån snabbhet.
    public Pokemon getFastestPokemon() {
        // Om båda är lika snabba avgör slumpen vem som börjar.
        if (playerPokemon.getSpeed() == wildPokemon.getSpeed()) {
            return Math.random() < 0.5
                    ? playerPokemon
                    : wildPokemon;
        }

        // Den Pokemon som har högst snabbhet attackerar först.
        if (playerPokemon.getSpeed() > wildPokemon.getSpeed()) {
            return playerPokemon;
        }
        // Om spelarens Pokemon inte är snabbast börjar motståndaren.
        return wildPokemon;
    }

    // Bestämmer båda deltagarnas turordning för den aktuella omgången.
    public Pokemon[] getTurnOrder() {
        // Hämtar den Pokemon som ska utföra sin handling först.
        Pokemon first = getFastestPokemon();
        // Den andra deltagaren placeras sist i turordningen.
        Pokemon second = first == playerPokemon
                ? wildPokemon
                : playerPokemon;

        return new Pokemon[]{first, second};
    }

    // Kontrollerar om striden är avslutad.
    // Striden slutar vid flykt eller när någon av Pokemon har besegras.
    public boolean isOver() {
        return fled
                || playerPokemon.isKnockedOut()
                || wildPokemon.isKnockedOut();
    }

    // Kontrollerar om spelaren vann genom att besegra motståndaren.
    // Spelarens Pokemon måste fortfarande vara stridsduglig.
    public boolean playerWon() {
        return wildPokemon.isKnockedOut()
                && !playerPokemon.isKnockedOut();
    }

    // Registrerar att spelaren flyr från striden.
    public void flee() {
        // Förhindrar att stridens tillstånd ändras efter att den avslutats.
        if (isOver()) {
            throw new IllegalStateException(
                    "Det går inte att fly från en avslutad strid."
            );
        }

        // Markerar striden som avslutad genom flykt.
        fled = true;
    }

    // Returnerar om spelaren har flytt från striden.
    public boolean hasFled() {
        return fled;
    }
}
