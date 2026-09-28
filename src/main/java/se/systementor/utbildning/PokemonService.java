package se.systementor.utbildning;

import se.systementor.utbildning.exception.InvalidAttackException;
import se.systementor.utbildning.exception.InvalidPokemonException;

import java.util.ArrayList;
import java.util.Scanner;

public class PokemonService {

    // Letar efter en Pokemon med samma namn, oavsett stora eller små bokstäver.
    public static Pokemon findPokemonByName(ArrayList<Pokemon> pokemons, String name) {
        for (Pokemon pokemon : pokemons) {
            if (pokemon.getName().equalsIgnoreCase(name)) {
                return pokemon;
            }
        }

        return null;
    }

    // Private eftersom metoden bara används i den här klassen.
    private static int getNextId(ArrayList<Pokemon> pokemons) {
        int maxId = 0;

        // Går igenom alla Pokemon för att hitta det högsta ID:t.
        for (Pokemon pokemon : pokemons) {
            if (pokemon.getId() > maxId) {
                maxId = pokemon.getId();
            }
        }

        // Returnerar nästa lediga ID genom att lägga till 1 på det högsta.
        return maxId + 1;
    }

    // Kontrollerar om ett Pokemon-namn redan finns i listan.
    private static boolean existsByName(ArrayList<Pokemon> pokemons, String name) {
        for (Pokemon pokemon : pokemons) {
            if (pokemon.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    // Kontrollerar om namnet används av någon annan Pokemon än den som redigeras.
    private static boolean existsByNameExceptId(
            ArrayList<Pokemon> pokemons,
            String name,
            int id
    ) {
        for (Pokemon pokemon : pokemons) {
            // Ignorerar den Pokemon som redigeras och kollar om någon annan har samma namn.
            if (pokemon.getId() != id &&
                    pokemon.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    // Visar alla Pokemon och deras attacker.
    public static void showAllPokemons(ArrayList<Pokemon> pokemons) {
        for (Pokemon pokemon : pokemons) {
            printDetails(pokemon);
            System.out.println();
        }
    }

    // Visar detaljer om vald Pokemon.
    public static void printDetails(Pokemon pokemon) {
        System.out.println(pokemon);
    }

    // Söker efter en Pokemon med hjälp av namn.
    public static void searchPokemon(ArrayList<Pokemon> pokemons, Scanner scanner) {
        System.out.println("Skriv namnet på en Pokemon:");
        String name = scanner.nextLine();

        Pokemon pokemon = findPokemonByName(pokemons, name);

        if (pokemon == null) {
            System.out.println("Pokemon hittades inte.");
            return;
        }

        printDetails(pokemon);
    }

    // Lägger till en ny Pokemon med validerad input.
    public static void addPokemon(ArrayList<Pokemon> pokemons, Scanner scanner) {
        // Hämtar nästa lediga ID så att varje Pokemon får ett eget ID.
        int id = getNextId(pokemons);

        System.out.println("Nästa Pokemon kommer få ID: " + id);

        String name = InputHelper.readValidName(scanner, "Namn: ");

        // Stoppar om namnet redan används av en annan Pokemon.
        if (existsByName(pokemons, name)) {
            System.out.println(name + " finns redan.");
            return;
        }

        // Läser in HP och ser till att värdet är mellan 1 och 999.
        int hp = InputHelper.readIntInRange(scanner, "HP: ", 1, 999);

        // Låter användaren välja Pokemon-typ.
        Type type = InputHelper.chooseType(scanner);

        try {
            Pokemon pokemon = new Pokemon(
                    id,
                    name,
                    type,
                    hp,
                    hp
            );

            // Skapar en lista för Pokemon-attacker.
            ArrayList<Attack> attacks = new ArrayList<>();

            // Skapar en standardattack beroende på Pokemon-typ.
            Attack attack = AttackService.createDefaultAttack(type);
            attacks.add(attack);

            pokemon.setAttacks(attacks);
            System.out.println("\n=== FÖRHANDSVISNING ===");
            printDetails(pokemon);

            boolean confirm = InputHelper.readYesNo(
                    scanner,
                    "\nBekräfta genom att skriva ja eller nej: "
            );

            // Lägger bara till Pokemon om användaren bekräftar.
            if (confirm) {
                pokemons.add(pokemon);

                PokemonFileService.savePokemonsToFile(pokemons);

                System.out.println("Pokemon tillagd och sparad.");
            } else {
                System.out.println("Avbrutet.");
            }
        } catch (InvalidAttackException e) {
            System.out.println("Det gick inte att lägga till attacken. " + e.getMessage());
        } catch (InvalidPokemonException e) {
            System.out.println("Det gick inte att lägga till pokemon. " + e.getMessage());
        }

    }

    // Ändrar namnet på en Pokemon.
    public static void editPokemonName(
            ArrayList<Pokemon> pokemons,
            Pokemon pokemon,
            Scanner scanner
    ) {
        String newName = InputHelper.readValidName(scanner, "Nytt namn: ");

        // Kontrollerar om namnet redan används av en annan Pokemon.
        if (existsByNameExceptId(pokemons, newName, pokemon.getId())) {
            System.out.println(newName + " finns redan.");
            return;
        }

        // Sparar det gamla namnet så att det kan visas i bekräftelsemeddelandet.
        String oldName = pokemon.getName();

        try {
            pokemon.setName(newName);
            System.out.println("Du ändrade " + oldName + " till " + newName + ".");
        } catch (InvalidPokemonException e) {
            System.out.println("Det gick inte att ändra namn på denna pokemon. " + e.getMessage());
        }

        System.out.println("\n=== FÖRHANDSVISNING ===");
        printDetails(pokemon);
    }

    // Ändrar typ på en Pokemon.
    public static void editPokemonType(Pokemon pokemon, Scanner scanner) {
        Type newType = InputHelper.chooseType(scanner);

        try {
            pokemon.setType(newType);
            System.out.println("Typen är uppdaterad.");
        } catch (InvalidPokemonException e) {
            System.out.println("Det gick inte att ändra typ. " + e.getMessage());
        }
        printDetails(pokemon);
    }

    // Ändrar currentHp på en Pokemon.
    public static void editPokemonCurrentHp(Pokemon pokemon, Scanner scanner) {
        int hp = InputHelper.readIntInRange(
                scanner,
                "Nytt nuvarande HP: ",
                0,
                pokemon.getMaxHp()
        );

        try {
            pokemon.setCurrentHp(hp);
            System.out.println("Nuvarande HP är uppdaterat.");
        } catch (InvalidPokemonException e) {
            System.out.println("Det gick inte att ändra nuvarande hp. " + e.getMessage());
        }
        printDetails(pokemon);
    }

    // Ändrar maxHp på en Pokemon.
    public static void editPokemonMaxHp(Pokemon pokemon, Scanner scanner) {
        int hp = InputHelper.readIntInRange(
                scanner,
                "Nytt max HP: ",
                1,
                999
        );

        try {
            pokemon.setMaxAndCurrentHp(hp);
            System.out.println("Max HP uppdaterades.");
        } catch (InvalidPokemonException e) {
            System.out.println("Max HP gick inte att ändra. " + e.getMessage());
        }
        printDetails(pokemon);
    }

    // Visar redigeringsmenyn och skickar vidare till rätt metod.
    public static void editPokemon(ArrayList<Pokemon> pokemons, Scanner scanner) {
        System.out.println("Ange namn på den Pokemon du vill redigera:");
        String name = scanner.nextLine();

        // Hämtar vald Pokemon innan redigeringsmenyn visas.
        Pokemon pokemon = findPokemonByName(pokemons, name);

        if (pokemon == null) {
            System.out.println(name + " hittades inte.");
            return;
        }

        System.out.println("\n=== FÖRHANDSVISNING ===");
        printDetails(pokemon);
        System.out.println();

        Menu.showEditMenu();

        int choice = InputHelper.readIntInRange(scanner, "Välj: ", 1, 7);

        // Skickar användaren vidare till rätt redigeringsmetod beroende på valet.
        switch (choice) {
            case 1 -> editPokemonName(pokemons, pokemon, scanner);
            case 2 -> editPokemonType(pokemon, scanner);
            case 3 -> editPokemonCurrentHp(pokemon, scanner);
            case 4 -> editPokemonMaxHp(pokemon, scanner);
            case 5 -> AttackService.addAttackToPokemon(pokemon, scanner);
            case 6 -> AttackService.removeAttackFromPokemon(pokemon, scanner);
            case 7 -> System.out.println("Går tillbaka till huvudmenyn.");
        }
    }

    // Tar bort en Pokemon efter bekräftelse.
    public static void removePokemon(ArrayList<Pokemon> pokemons, Scanner scanner) {

        System.out.println("Ange namn på den Pokemon du vill ta bort:");
        String name = scanner.nextLine();

        // Hämtar den Pokemon som användaren vill ta bort.
        Pokemon pokemon = findPokemonByName(pokemons, name);

        if (pokemon == null) {
            System.out.println("Pokemon hittades inte.");
            return;
        }

        System.out.println("Du är på väg att ta bort:");
        System.out.println(pokemon.getId() + ". " + pokemon.getName());

        boolean confirm = InputHelper.readYesNo(
                scanner,
                "Bekräfta genom att skriva ja eller nej: "
        );

        // Tar bara bort Pokemon om användaren bekräftar.
        if (confirm) {
            pokemons.remove(pokemon);

            System.out.println(pokemon.getName() + " har tagits bort.");
        } else {
            System.out.println("Borttagningen avbröts.");
        }
    }
}