# Pokedex

Ett konsolbaserat Java-program för att hantera Pokemon och deras attacker.

## Köra programmet

Projektet är ett Maven-projekt och kan köras i IntelliJ IDEA.

1. Klona projektet från GitHub:

```
git clone https://github.com/figge94/pokedex.git
```

2. Öppna projektet i IntelliJ IDEA.
3. Låt Maven läsa in projektets dependencies.
4. Kör `Main`-klassen.

Programmet kan även köras via Maven från terminalen om projektet är konfigurerat för det.

## Exempel på användning

När programmet startas första gången finns startdatan redan i `pokemon-seed.json`.

Om ingen sparad fil finns laddas Pokemon automatiskt in från seed-filen.

Därefter visas huvudmenyn där användaren kan välja mellan olika funktioner:

- **Visa alla Pokemon** visar alla Pokemon som finns i Pokedexen.
- **Sök Pokemon** låter användaren söka efter en Pokemon genom att skriva in dess namn.
- **Lägg till Pokemon** skapar först ett nytt ID. Därefter får användaren skriva in namn, HP och typ. Om namnet redan finns går Pokemon inte att lägga till. När en typ väljs får Pokemon automatiskt en standardattack som hör till den valda typen.
- **Redigera Pokemon** börjar med att användaren skriver namnet på den Pokemon som ska redigeras. En förhandsvisning visas och därefter öppnas en meny där användaren kan ändra namn, typ, nuvarande HP, max HP samt lägga till eller ta bort attacker.
- **Ta bort Pokemon** låter användaren välja en Pokemon som ska tas bort och bekräfta borttagningen.
- **Spara till fil** sparar den aktuella Pokemon-datan och skapar `pokemon.json`.
- **Ladda från fil** läser in sparad data från `pokemon.json`.
- **Återställ seed-data** laddar in startdatan från `pokemon-seed.json` igen. Innan återställningen genomförs får användaren bekräfta om den återställda datan ska sparas eller inte.

Efter att Pokemon har lagts till, redigerats eller tagits bort kan användaren välja **Spara till fil** för att spara ändringarna i `pokemon.json`.

## Projektstruktur

| Klass                | Ansvar                                      |
|:---------------------|:--------------------------------------------|
| `Main`               | Startar programmet och hanterar huvudmenyn  |
| `Pokemon`            | Modellklass för Pokemon                     |
| `Attack`             | Modellklass för attacker                    |
| `PokemonService`     | Hanterar logik för Pokemon                  |
| `AttackService`      | Hanterar logik för attacker                 |
| `PokemonFileService` | Hanterar läsning och skrivning av JSON-data |
| `InputHelper`        | Hanterar och validerar användarinput        |
| `Menu`               | Visar programmets menyer                    |

## Filhantering

Programmet använder JSON-filer för att spara och läsa Pokemon-data.

- `pokemon.json` används för sparad data
- `pokemon-seed.json` används som startdata om ingen sparad fil finns och innehåller 9 fördefinierade Pokemon med varsin attack, där alla nio typer finns representerade
- Om ingen sparad fil finns vid programstart laddas startdata automatiskt
- Pokemon-data sparas automatiskt till `pokemon.json` när programmet avslutas

Jackson används för att konvertera mellan Java-objekt och JSON.