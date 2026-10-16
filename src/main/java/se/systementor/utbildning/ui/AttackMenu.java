package se.systementor.utbildning.ui;

import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.exception.PokemonSaveException;
import se.systementor.utbildning.model.Attack;
import se.systementor.utbildning.model.Pokemon;
import se.systementor.utbildning.model.Type;
import se.systementor.utbildning.service.AttackService;
import se.systementor.utbildning.service.PokemonService;

import java.util.List;
import java.util.Scanner;

// Hanterar inmatning och menyval för att lägga till och ta bort attacker.
public class AttackMenu {
    private final PokemonService pokemonService;

    // Tar emot PokemonService via Dependency Injection.
    public AttackMenu(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    // Läser in och lägger till en ny attack på en Pokemon.
    public void handleAddAttack(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        // Läser in attackens namn och validerar inmatningen.
        String attackName =
                InputHelper.readValidName(scanner, "Attacknamn: ");

        // Läser in attackens grundskada.
        // InputHelper ser till att värdet ligger mellan 1 och 999.
        int damage =
                InputHelper.readIntInRange(
                        scanner,
                        "Skada: ",
                        1,
                        999
                );

        // Läser in träffsäkerheten som ett procentvärde.
        // Värdet måste vara mellan 0 och 100.
        int accuracy =
                InputHelper.readIntInRange(
                        scanner,
                        "Träffsäkerhet (0-100): ",
                        0,
                        100
                );

        // Låter användaren välja vilken typ attacken ska ha.
        Type attackType =
                InputHelper.chooseType(scanner);

        // AttackService skapar attacken så att UI inte behöver känna till DamageAttack.
        Attack newAttack = AttackService.createAttack(
                attackName,
                damage,
                accuracy,
                attackType
        );

        try {
            pokemonService.addAttackToPokemon(
                    pokemons,
                    pokemon,
                    newAttack
            );

            Messages.success(
                    pokemon.getName()
                            + " har fått attacken "
                            + newAttack.getName()
            );

        } catch (InvalidAttackException | PokemonSaveException e) {
            Messages.error("Fel: " + e.getMessage());
        }
    }

    // Låter användaren välja en attack att ta bort.
    public void removeAttackFromPokemon(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        List<Attack> attacks = pokemon.getAttacks();

        // En Pokemon måste ha minst en attack kvar.
        if (attacks.size() <= 1) {
            Messages.warning("En Pokemon måste ha minst 1 attack.");
            return;
        }

        System.out.println("Välj attack att ta bort:");

        // Går igenom listan och skriver ut varje attack med ett nummer.
        // Nummereringen börjar på 1 för att vara tydlig för användaren.
        for (int i = 0; i < attacks.size(); i++) {
            System.out.println((i + 1) + ": " + attacks.get(i).getName());
        }

        // Listans index börjar på 0, medan menyvalen börjar på 1.
        int attackChoice = InputHelper.readIntInRange(
                scanner,
                "Välj: ",
                1,
                attacks.size()
        );

        Attack attackToRemove = attacks.get(attackChoice - 1);

        try {
            // Skickar attacken till PokemonService för borttagning.
            // Servicen ansvarar för att ändra Pokemon och spara resultatet.
            pokemonService.removeAttackFromPokemon(
                    pokemons,
                    pokemon,
                    attackToRemove
            );

            // Bekräftar att attacken har tagits bort.
            Messages.success(
                    attackToRemove.getName() + " har tagits bort."
            );

        } catch (InvalidAttackException | PokemonSaveException e) {

            // Visar ett felmeddelande om attacken inte kan tas bort
            // eller om den uppdaterade Pokemon-listan inte kan sparas.
            Messages.error(
                    "Det gick inte att ta bort attacken. " + e.getMessage()
            );
        }
    }
}
