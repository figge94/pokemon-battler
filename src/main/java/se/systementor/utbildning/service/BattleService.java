package se.systementor.utbildning.service;

import se.systementor.utbildning.model.Battle;
import se.systementor.utbildning.model.BattleAction;
import se.systementor.utbildning.model.BattleStats;
import se.systementor.utbildning.model.CpuPlayer;
import se.systementor.utbildning.model.FleeResult;
import se.systementor.utbildning.model.ActionResult;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.repository.BattleStatsFileService;
import se.systementor.utbildning.ui.BattleMenu;
import se.systementor.utbildning.ui.BattlePresentation;
import se.systementor.utbildning.ui.Messages;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

// Samordnar striden mellan spelaren och CPU:n.
// Använder andra klasser för handlingar, presentation och statistik.
public class BattleService {

    private final BattleStats battleStats;
    private final BattleTurnService battleTurnService;

    // Dependency Injection: statistik och CPU-spelare
    // skickas in via konstruktorn i stället för att skapas här.
    public BattleService(BattleStats battleStats, CpuPlayer cpuPlayer) {
        this.battleStats = battleStats;

        // Skapar tjänsten som ansvarar för handlingarna under strid.
        this.battleTurnService = new BattleTurnService(battleStats, cpuPlayer);
    }

    // static final gör att samma slumpgenerator återanvänds.
    private static final Random RANDOM = new Random();

    // Väljer en slumpmässig motståndare som inte är spelarens Pokemon.
    public static Pokemon chooseRandomWildPokemon(
            List<Pokemon> pokemons,
            Pokemon playerPokemon
    ) {
        if (pokemons == null || playerPokemon == null) {
            throw new IllegalArgumentException(
                    "Pokemon-listan och spelarens Pokemon får inte vara null."
            );
        }

        if (pokemons.size() < 2) {
            throw new IllegalArgumentException(
                    "Det måste finnas minst två Pokemon för att starta en strid."
            );
        }

        // Stream och anyMatch kontrollerar om minst en annan Pokemon finns.
        // equals jämför Pokemon utifrån ID.
        boolean hasOpponent = pokemons.stream()
                .anyMatch(pokemon ->
                        pokemon != null && !pokemon.equals(playerPokemon)
                );

        if (!hasOpponent) {
            throw new IllegalArgumentException(
                    "Det finns ingen annan Pokemon att möta."
            );
        }

        Pokemon wildPokemon;

        // Slumpar tills en Pokemon med ett annat ID hittas.
        do {
            int index = RANDOM.nextInt(pokemons.size());
            wildPokemon = pokemons.get(index);
        } while (wildPokemon == null || wildPokemon.equals(playerPokemon));

        return wildPokemon;
    }

    // Hanterar en strid tills någon besegras eller spelaren flyr.
    public void startBattle(
            List<Pokemon> pokemons,
            Pokemon playerPokemon,
            Scanner scanner
    ) {
        // Deklareras utanför try så att HP kan återställas i finally.
        Pokemon wildPokemon = null;

        try {
            wildPokemon = chooseRandomWildPokemon(
                    pokemons, playerPokemon
            );

            // Båda deltagarna måste ha minst en attack.
            if (playerPokemon.getAttacks().isEmpty()
                    || wildPokemon.getAttacks().isEmpty()) {
                throw new IllegalArgumentException(
                        "Båda Pokemon måste ha minst en attack för att strida."
                );
            }

            // Battle håller reda på deltagare, turordning och stridens tillstånd.
            Battle battle = new Battle(
                    playerPokemon,
                    wildPokemon
            );

            BattleMenu.showBattleStart(
                    battle.getPlayerPokemon(),
                    battle.getWildPokemon()
            );

            while (!battle.isOver()) {

                // Spelarens val kan vara en attack eller flykt.
                BattleAction playerAction = BattleMenu.chooseAction(
                        playerPokemon,
                        scanner
                );

                // endsBattle gör att olika handlingar själva kan ange
                // om de avslutar striden.
                if (playerAction.endsBattle()) {

                    ActionResult result = playerAction.execute(
                            playerPokemon,
                            wildPokemon
                    );

                    // instanceof kontrollerar om resultatet är ett FleeResult.
                    if (result instanceof FleeResult) {
                        battle.flee();
                        break;
                    }

                    // Skyddar mot avslutande handlingar med oväntade resultat.
                    throw new IllegalStateException(
                            "Handlingen avslutar striden utan ett giltigt resultat."
                    );
                }

                // Snabbheten avgör turordningen, slumpen avgör vid lika snabbhet.
                Pokemon[] turnOrder = battle.getTurnOrder();

                BattlePresentation.showFirstAttacker(turnOrder[0]);

                for (Pokemon attacker : turnOrder) {

                    // Förhindrar att någon attackerar efter att striden avslutats.
                    if (battle.isOver()) {
                        break;
                    }

                    // == jämför objektreferenser för att avgöra vems tur det är.
                    if (attacker == playerPokemon) {

                        BattlePresentation.showTurn(playerPokemon, true);
                        BattlePresentation.pause();

                        // Låter BattleTurnService utföra spelarens tur.
                        battleTurnService.playerTurn(
                                playerPokemon,
                                wildPokemon,
                                playerAction
                        );

                    } else {

                        BattlePresentation.showTurn(wildPokemon, false);
                        BattlePresentation.pause();

                        // Låter BattleTurnService utföra CPU:ns tur.
                        battleTurnService.wildTurn(
                                wildPokemon,
                                playerPokemon
                        );
                    }

                    BattleMenu.showBattleHp(playerPokemon, wildPokemon);
                    BattlePresentation.pause();
                }

                // Visar den besegrade Pokemonen om spelaren inte har flytt.
                if (battle.isOver() && !battle.hasFled()) {

                    // Ternary väljer den Pokemon som har noll HP.
                    Pokemon knockedOut = playerPokemon.isKnockedOut()
                            ? playerPokemon
                            : wildPokemon;

                    BattleMenu.showKnockedOut(knockedOut);
                }
            }

            // Visar olika slutresultat beroende på om spelaren flydde.
            if (battle.hasFled()) {
                BattlePresentation.showFlee();
            } else {
                BattleMenu.showBattleResult(
                        battle.getPlayerPokemon(),
                        battle.getWildPokemon()
                );
            }

            // Registrerar stridens resultat och sparar statistiken.
            saveBattleStatistics(battle, playerPokemon);

        } catch (IllegalArgumentException e) {
            Messages.error(
                    "Ett fel uppstod under striden: " + e.getMessage()
            );

        } finally {
            // finally körs även om ett fel inträffar och återställer HP.
            if (playerPokemon != null) {
                playerPokemon.restoreHealth();
            }

            if (wildPokemon != null) {
                wildPokemon.restoreHealth();
            }
        }
    }

    // Ger spelmenyn tillgång till den aktuella statistiken.
    public BattleStats getBattleStats() {
        return battleStats;
    }

    // Registrerar vinst, förlust eller flykt.
    private void registerBattleResult(
            Battle battle,
            Pokemon playerPokemon
    ) {
        battleStats.registerPokemonUsage(playerPokemon.getId());

        if (battle.hasFled()) {
            battleStats.addFlee();

        } else if (battle.playerWon()) {
            battleStats.addWin(playerPokemon.getId());

        } else {
            battleStats.addLoss(playerPokemon.getId());
        }
    }

    // Registrerar stridsresultatet och sparar statistiken till JSON.
    private void saveBattleStatistics(
            Battle battle,
            Pokemon playerPokemon
    ) {
        registerBattleResult(battle, playerPokemon);

        // Filhanteringen sköts av BattleStatsFileService.
        BattleStatsFileService.saveStats(battleStats);
    }
}