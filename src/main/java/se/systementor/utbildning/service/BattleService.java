package se.systementor.utbildning.service;

import se.systementor.utbildning.model.*;
import se.systementor.utbildning.ui.BattleMenu;
import se.systementor.utbildning.repository.BattleStatsFileService;
import se.systementor.utbildning.ui.Messages;
import se.systementor.utbildning.ui.BattlePresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// Ansvarar för att samordna en hel strid mellan spelaren och CPU:n.
// Använder andra klasser för handlingar, presentation och statistik
// så att all funktionalitet inte behöver ligga i samma klass.
public class BattleService {

    // Statistik och CPU-spelare skickas in via konstruktorn.
    // Detta är Dependency Injection och gör att samma objekt
    // kan återanvändas under programmets körning.
    private final BattleStats battleStats;
    private final CpuPlayer cpuPlayer;

    // Skapar stridstjänsten med de beroenden den behöver.
    public BattleService(BattleStats battleStats, CpuPlayer cpuPlayer) {
        this.battleStats = battleStats;
        this.cpuPlayer = cpuPlayer;
    }

    // Gemensam slumpgenerator som används när en vild Pokemon väljs.
    private static final Random RANDOM = new Random();

    // Väljer en slumpmässig motståndare som inte är spelarens Pokemon.
    public static Pokemon chooseRandomWildPokemon(
            List<Pokemon> pokemons,
            Pokemon playerPokemon
    ) {
        // Kontrollerar att både listan och spelarens Pokemon finns.
        if (pokemons == null || playerPokemon == null) {
            throw new IllegalArgumentException(
                    "Pokemon-listan och spelarens Pokemon får inte vara null."
            );
        }

        // En strid kräver minst två Pokemon i samlingen.
        if (pokemons.size() < 2) {
            throw new IllegalArgumentException(
                    "Det måste finnas minst två Pokemon för att starta en strid."
            );
        }

        // Streams kontrollerar att det finns minst en annan Pokemon.
        // anyMatch returnerar true så fort en giltig motståndare hittas.
        boolean hasOpponent = pokemons.stream()
                .anyMatch(pokemon ->
                        pokemon != null && !pokemon.equals(playerPokemon)
                );

        // Förhindrar att slumpningen fortsätter om ingen
        // giltig motståndare finns i samlingen.
        if (!hasOpponent) {
            throw new IllegalArgumentException(
                    "Det finns ingen annan Pokemon att möta."
            );
        }

        Pokemon wildPokemon;

        // Slumpar en Pokemon tills den inte är null
        // och inte har samma ID som spelarens Pokemon.
        do {
            int index = RANDOM.nextInt(pokemons.size());
            wildPokemon = pokemons.get(index);
        } while (wildPokemon == null || wildPokemon.equals(playerPokemon));

        // Returnerar motståndaren som ska användas i striden.
        return wildPokemon;
    }

    // Utför en stridshandling genom interfacet BattleAction.
    // Tack vare polymorfism anropas rätt execute-metod
    // oavsett vilken konkret handling som skickas in.
    private static ActionResult executeAction(
            BattleAction action,
            Pokemon attacker,
            Pokemon defender
    ) {
        return action.execute(attacker, defender);
    }

    // Hanterar spelarens tur och registrerar använda attacker.
    private void playerTurn(
            Pokemon playerPokemon,
            Pokemon wildPokemon,
            BattleAction playerAction
    ) {
        // Utför den handling som spelaren tidigare valt.
        ActionResult result = executeAction(
                playerAction,
                playerPokemon,
                wildPokemon
        );

        // Skickar resultatet till presentationsklassen.
        // BattleService behöver därför inte skriva ut
        // attackens detaljer direkt i konsolen.
        BattlePresentation.showActionResult(
                playerAction,
                result,
                playerPokemon,
                wildPokemon
        );

        // Registrerar namnet på spelarens använda attack.
        // Statistiken kan senare visa vilka attacker som används mest.
        battleStats.registerAttackUsage(playerAction.label());
    }

    // Hanterar CPU:ns tur med hjälp av dess valda strategi.
    private void wildTurn(
            Pokemon wildPokemon,
            Pokemon playerPokemon
    ) {
        // Hämtar CPU-Pokemonens attacker som BattleAction-objekt.
        // Listan kan därför skickas till CpuPlayer via interfacet.
        List<BattleAction> actions =
                new ArrayList<>(wildPokemon.getAttacks());

        // CpuPlayer delegerar valet till sin CpuStrategy.
        // Exempelvis kan RandomStrategy välja en slumpmässig attack.
        BattleAction selectedAction = cpuPlayer.chooseAction(actions);

        // Utför den valda handlingen genom BattleAction.
        ActionResult result = executeAction(
                selectedAction,
                wildPokemon,
                playerPokemon
        );

        // Visar vad CPU:n gjorde och resultatet av handlingen.
        BattlePresentation.showActionResult(
                selectedAction,
                result,
                wildPokemon,
                playerPokemon
        );
    }

    // Startar och hanterar en hel strid tills någon besegras
    // eller spelaren väljer att fly.
    public void startBattle(
            List<Pokemon> pokemons,
            Pokemon playerPokemon,
            Scanner scanner
    ) {
        // Deklareras utanför try så att motståndaren också
        // kan få sin hälsa återställd i finally-blocket.
        Pokemon wildPokemon = null;

        try {
            // Väljer en slumpmässig giltig motståndare.
            wildPokemon = chooseRandomWildPokemon(
                    pokemons, playerPokemon
            );

            // Kontrollerar att båda Pokemon har attacker.
            // Det förhindrar att en strid startas utan giltiga handlingar.
            if (playerPokemon.getAttacks().isEmpty()
                    || wildPokemon.getAttacks().isEmpty()) {
                throw new IllegalArgumentException(
                        "Båda Pokemon måste ha minst en attack för att strida."
                );
            }

            // Skapar ett Battle-objekt som håller reda på
            // deltagare, turordning och om striden är avslutad.
            Battle battle = new Battle(
                    playerPokemon,
                    wildPokemon
            );

            // Visar vilka Pokemon som ska mötas.
            BattleMenu.showBattleStart(
                    battle.getPlayerPokemon(),
                    battle.getWildPokemon()
            );

            // Striden fortsätter så länge ingen har besegrats
            // och spelaren inte har flytt.
            while (!battle.isOver()) {

                // Låter spelaren välja en handling från stridsmenyn.
                // Valet kan vara en attack eller exempelvis flykt.
                BattleAction playerAction = BattleMenu.chooseAction(
                        playerPokemon,
                        scanner
                );

                // Kontrollerar om handlingen ska avsluta striden.
                // endsBattle är en metod i BattleAction som olika
                // handlingar kan implementera på olika sätt.
                if (playerAction.endsBattle()) {

                    // Utför handlingen och hämtar dess resultat.
                    ActionResult result = playerAction.execute(
                            playerPokemon,
                            wildPokemon
                    );

                    // Kontrollerar att resultatet verkligen är ett flyktresultat.
                    if (result instanceof FleeResult) {

                        // Registrerar flykten i Battle och lämnar stridsloopen.
                        battle.flee();
                        break;
                    }

                    // Skyddar stridslogiken om en avslutande handling
                    // returnerar ett resultat som inte kan hanteras.
                    throw new IllegalStateException(
                            "Handlingen avslutar striden utan ett giltigt resultat."
                    );
                }

                // Hämtar turordningen utifrån Pokemonens snabbhet.
                // Om snabbheten är lika får slumpen avgöra.
                Pokemon[] turnOrder = battle.getTurnOrder();

                // Visar vilken Pokemon som ska utföra första handlingen.
                BattlePresentation.showFirstAttacker(turnOrder[0]);

                // Går igenom båda deltagarna i den bestämda turordningen.
                for (Pokemon attacker : turnOrder) {

                    // Avbryter om striden redan avslutats.
                    // Detta förhindrar att den andra Pokemonen
                    // attackerar efter att någon blivit besegrad.
                    if (battle.isOver()) {
                        break;
                    }

                    // Avgör om det är spelarens eller CPU:ns tur.
                    // Själva handlingen utförs i separata metoder.
                    if (attacker == playerPokemon) {

                        // Visar att spelaren ska utföra sin handling.
                        BattlePresentation.showTurn(playerPokemon, true);
                        BattlePresentation.pause();

                        playerTurn(playerPokemon, wildPokemon, playerAction);

                    } else {

                        // Visar att CPU:n ska utföra sin handling.
                        BattlePresentation.showTurn(wildPokemon, false);
                        BattlePresentation.pause();

                        wildTurn(wildPokemon, playerPokemon);
                    }

                    // Visar båda deltagarnas HP efter handlingen.
                    BattleMenu.showBattleHp(playerPokemon, wildPokemon);
                    BattlePresentation.pause();
                }

                // Visar vilken Pokemon som blev besegrad.
                // Meddelandet visas bara om striden inte avslutades via flykt.
                if (battle.isOver() && !battle.hasFled()) {

                    // Väljer den Pokemon som har noll HP.
                    Pokemon knockedOut = playerPokemon.isKnockedOut()
                            ? playerPokemon
                            : wildPokemon;

                    BattleMenu.showKnockedOut(knockedOut);
                }
            }

            // Visar stridens slutresultat beroende på
            // om spelaren flydde eller om någon Pokemon besegrades.
            if (battle.hasFled()) {
                BattlePresentation.showFlee();
            } else {
                BattleMenu.showBattleResult(
                        battle.getPlayerPokemon(),
                        battle.getWildPokemon()
                );
            }

            // Registrerar stridsresultatet och sparar statistiken till JSON.
            saveBattleStatistics(battle, playerPokemon);

        } catch (IllegalArgumentException e) {

            // Visar ett begripligt felmeddelande om striden
            // inte kan startas eller ett ogiltigt värde upptäcks.
            Messages.error(
                    "Ett fel uppstod under striden: " + e.getMessage()
            );

        } finally {

            // Återställer båda Pokemonens HP även om
            // striden avbryts på grund av ett fel.
            if (playerPokemon != null) {
                playerPokemon.restoreHealth();
            }

            if (wildPokemon != null) {
                wildPokemon.restoreHealth();
            }
        }
    }

    // Returnerar statistikobjektet som används av stridssystemet.
    // Spelmenyn kan därmed visa aktuella resultat.
    public BattleStats getBattleStats() {
        return battleStats;
    }

    // Registrerar hur striden avslutades i statistikobjektet.
    private void registerBattleResult(
            Battle battle,
            Pokemon playerPokemon
    ) {
        // Registrerar att spelaren använde denna Pokemon.
        battleStats.registerPokemonUsage(playerPokemon.getId());

        // Uppdaterar rätt statistik beroende på stridens utfall.
        if (battle.hasFled()) {

            // Spelaren flydde och får varken vinst eller förlust.
            battleStats.addFlee();

        } else if (battle.playerWon()) {

            // Motståndaren besegrades och spelaren får en vinst.
            battleStats.addWin(playerPokemon.getId());

        } else {

            // Spelarens Pokemon besegrades och en förlust registreras.
            battleStats.addLoss(playerPokemon.getId());
        }
    }

    // Samlar registrering och sparande av stridsstatistik på ett ställe.
    private void saveBattleStatistics(
            Battle battle,
            Pokemon playerPokemon
    ) {
        // Uppdaterar statistikobjektet med resultatet från striden.
        registerBattleResult(battle, playerPokemon);

        // Sparar samma statistikobjekt som spelmenyn använder.
        // Filhanteringen utförs av BattleStatsFileService.
        BattleStatsFileService.saveStats(battleStats);
    }
}
