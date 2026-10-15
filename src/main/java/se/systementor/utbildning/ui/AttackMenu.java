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

// Ansvarar för menyerna där användaren kan lägga till och ta bort attacker.
// Klassen hanterar inmatning och presentation, medan PokemonService
// ansvarar för ändringar och sparande av Pokemon-data.
public class AttackMenu {

    // Lagrar den service som används för att ändra Pokemonens attacker.
    // Fältet är privat och final för att skydda referensen.
    private final PokemonService pokemonService;

    // Tar emot PokemonService via konstruktorn.
    // Detta är Dependency Injection eftersom beroendet skapas utanför klassen.
    public AttackMenu(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    // Låter användaren skapa och lägga till en ny attack på en Pokemon.
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

        // Skapar attacken genom AttackService.
        // Menyn behöver därför inte själv skapa en DamageAttack direkt.
        Attack newAttack = AttackService.createAttack(
                attackName,
                damage,
                accuracy,
                attackType
        );

        try {
            // Skickar attacken till PokemonService.
            // Servicen lägger till attacken och sparar Pokemon-listan.
            pokemonService.addAttackToPokemon(
                    pokemons,
                    pokemon,
                    newAttack
            );

            // Visar ett meddelande när attacken har lagts till.
            Messages.success(
                    pokemon.getName()
                            + " har fått attacken "
                            + newAttack.getName()
            );

        } catch (InvalidAttackException | PokemonSaveException e) {

            // Hanterar både ogiltiga attacker och problem vid sparande.
            // UI ansvarar för att visa felet för användaren.
            Messages.error("Fel: " + e.getMessage());
        }

        // Visar Pokemonens aktuella information efter försöket.
        // Använder samma utskriftsmetod som resten av Pokemon-menyn.
        PokemonMenu.printDetails(pokemon);
    }

    // Låter användaren välja och ta bort en attack från en Pokemon.
    public void removeAttackFromPokemon(
            List<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        // Hämtar en skrivskyddad kopia av Pokemonens attacker.
        // Den riktiga attacklistan kan bara ändras genom Pokemonens metoder.
        List<Attack> attacks = pokemon.getAttacks();

        // Kontrollerar att Pokemon har fler än en attack.
        // Den sista attacken får inte tas bort enligt spelets regler.
        if (attacks.size() <= 1) {
            Messages.warning("En Pokemon måste ha minst 1 attack.");
            return;
        }

        // Visar vilka attacker användaren kan välja att ta bort.
        System.out.println("Välj attack att ta bort:");

        // Går igenom listan och skriver ut varje attack med ett nummer.
        // Nummereringen börjar på 1 för att vara tydlig för användaren.
        for (int i = 0; i < attacks.size(); i++) {
            System.out.println((i + 1) + ": " + attacks.get(i).getName());
        }

        // Läser in användarens val och kontrollerar att numret
        // motsvarar en attack som finns i listan.
        int attackChoice = InputHelper.readIntInRange(
                scanner,
                "Välj: ",
                1,
                attacks.size()
        );

        // Hämtar den valda attacken.
        // Minus ett behövs eftersom listans index börjar på 0.
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

        // Visar samma typ av rubrik som övriga menyer.
        ConsoleUI.header("FÖRHANDSVISNING");
        PokemonMenu.printDetails(pokemon);
    }
}
