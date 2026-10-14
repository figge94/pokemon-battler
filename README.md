# PokemonBattler

Ett konsolbaserat Pokemon-spel utvecklat i Java 21 där användaren kan hantera sin Pokédex, skapa och redigera Pokémon, delta i turbaserade strider och följa sin stridsstatistik.

## Köra programmet

Projektet använder Java 21 och Maven. Starta programmet genom att köra Main-klassen i IntelliJ IDEA.

1. Klona projektet från GitHub:
```
git clone https://github.com/figge94/pokemon-battler.git
```
2. Öppna projektet i IntelliJ IDEA.
3. Låt Maven läsa in projektets dependencies.
4. Kör `Main`-klassen.

## Exempel på användning

När programmet startas första gången finns startdatan redan i `pokemon-seed.json`.

Om ingen sparad fil finns laddas Pokemon automatiskt in från seed-filen.

Därefter visas huvudmenyn där användaren kan välja mellan olika funktioner:

- **Visa alla Pokemon** visar alla Pokemon som finns i Pokedexen.
- **Spela** öppnar en meny där användaren kan starta strider och visa stridsstatistik.
- **Sök Pokemon** låter användaren söka efter en Pokemon genom att skriva in dess namn.
- **Lägg till Pokemon** skapar först ett nytt ID. Därefter får användaren skriva in namn, HP och typ. Om namnet redan finns går Pokemon inte att lägga till. När en typ väljs får Pokemon automatiskt en standardattack som hör till den valda typen.
- **Redigera Pokemon** börjar med att användaren skriver namnet på den Pokemon som ska redigeras. En förhandsvisning visas och därefter öppnas en meny där användaren kan ändra namn, typ, nuvarande HP, max HP samt lägga till eller ta bort attacker.
- **Ta bort Pokemon** låter användaren välja en Pokemon som ska tas bort och bekräfta borttagningen.
- **Spara till fil** sparar den aktuella Pokemon-datan och skapar `pokemon.json`.
- **Ladda från fil** läser in sparad data från `pokemon.json`.
- **Återställ seed-data** laddar in startdata från `pokemon-seed.json` igen. Innan återställningen genomförs får användaren bekräfta om den återställda datan ska sparas eller inte.

Efter att Pokemon har lagts till, redigerats eller tagits bort kan användaren välja **Spara till fil** för att spara ändringarna i `pokemon.json`.

## Stridssystem

Under Spela kan användaren starta en strid mot en slumpmässigt vald Pokemon från Pokedexen.

Spelaren väljer först vilken Pokemon som ska användas. Motståndaren väljs automatiskt och kan inte vara samma Pokémon som spelarens.

Striden är turbaserad och fungerar så här:

- Spelaren väljer en attack varje omgång.
- Pokemonens Speed avgör vem som attackerar först. Vid lika Speed avgör slumpen.
- Motståndaren väljer en slumpmässig attack.
- En attack kan missa beroende på dess träffsäkerhet.
- Skadan påverkas av attackens typ, slumpvariation och eventuella kritiska träffar.
- Striden avslutas när en Pokemon har 0 HP.

Efter varje strid visas resultatet och statistiken uppdateras. Pokemonens HP återställs efter avslutad strid.

## Skadeberäkning

Skadan beräknas med följande formel:

Basskada × typeffektivitet × slumpfaktor × kritisk faktor

- Basskada: Attackens grundskada.
- Typeffektivitet: 2,0 vid typfördel, 0,5 vid typnackdel och annars 1,0 för de typkombinationer som programmet hanterar.
- Slumpfaktor: Mellan 0,85 och 1,00, vilket ger variation mellan attacker.
- Kritisk faktor: En kritisk träff har 1/16 chans att inträffa och fördubblar skadan. Annars är faktorn 1.

Resultatet omvandlas till ett heltal innan skadan dras från motståndarens HP.

## Stridsstatistik

Programmet sparar statistik över spelarens strider.

I menyn **Spela** kan användaren välja att visa:

- **Total statistik:** Totalt antal strider, vinster och förluster.
- **Statistik per Pokémon:** Antal vinster, förluster, vinstprocent och win/loss-ratio för varje Pokémon som deltagit i strider.
- **Mest använda Pokémon:** Vilken Pokémon spelaren har använt flest gånger och antal strider.
- **Mest använda attack:** En lista över spelarens attacker, sorterade efter hur många gånger de använts.

Vinstprocenten beräknas enligt:

**Vinster / (Vinster + Förluster) × 100**

Win/loss-ratio beräknas enligt:

**Vinster / Förluster**

Om en Pokémon har vinster men inga förluster visas ratio som oändlig (`∞`).

Statistiken sparas automatiskt efter varje avslutad strid i `battle-stats.json` med hjälp av Jackson.

Statistiken kopplas till Pokemonens ID istället för namn, så att tidigare resultat bevaras om en Pokémon byter namn.

Om statistikfilen saknas skapas ny statistik automatiskt.

## Projektstruktur

| Paket           | Klasser                                                              | Ansvar                                                       |
|-----------------|----------------------------------------------------------------------|--------------------------------------------------------------|
| Grundpaketet    | `Main`                                                               | Startar programmet och hanterar huvudmenyn                   |
| `model`         | `Pokemon`, `Type`, `Attack`, `DamageAttack`, `Battle`, `BattleStats` | Representerar Pokémon, attacker, strider och statistik       |
| `service`       | `PokemonService`, `AttackService`, `BattleService`                   | Hanterar programmets logik                                   |
| `repository`    | `PokemonFileService`, `BattleStatsFileService`                       | Läser och sparar JSON-filer                                  |
| `ui`            | `Menu`, `PokemonMenu`, `AttackMenu`, `BattleMenu`                    | Hanterar menyer och användargränssnitt                       |
| `ui`            | `ConsoleUI`, `ConsoleColors`, `InputHelper`, `Messages`              | Hanterar konsolutskrifter, färger, inmatning och meddelanden |
| `exception`     | Egna undantagsklasser                                                | Hanterar specifika fel i programmet                          |
| `src/test/java` | `PokemonTest`                                                        | Innehåller tester för Pokémon-klassen                        |

## Objektorienterad programmering (OOP)

Projektet använder objektorienterad programmering för att dela upp
ansvar mellan olika klasser.

### Inkapsling
I `Pokemon` är attributen privata och nås genom metoder.
Det gör att klassen själv kan kontrollera exempelvis HP och
vilka attacker en Pokemon får ha.

### Arv och abstraktion
`Attack` är en abstrakt klass som innehåller gemensamma
egenskaper för attacker, exempelvis namn, typ, skada
och träffsäkerhet.

`DamageAttack` ärver från `Attack` och implementerar
hur en attack orsakar skada.

### Polymorfism
Programmet hanterar attacker genom typen `Attack`,
även när objektet är en `DamageAttack`.

Vid strid anropas `execute()` på attackobjektet.
Det gör att programmet kan utökas med fler attacktyper
som implementerar `execute()` på olika sätt.

### Enum
`Type` är en enum som innehåller Pokemonens olika typer.
Det förhindrar att ogiltiga typnamn används i programmet.

## Filhantering

Programmet använder JSON och biblioteket Jackson för att
spara och läsa data.

### Pokemon-data

- `pokemon-seed.json` innehåller 9 fördefinierade Pokemon med
  olika typer och standardattacker.
- `pokemon.json` innehåller användarens sparade Pokemon.
- Om `pokemon.json` saknas vid programstart laddas
  startdatan från `pokemon-seed.json`.
- Användaren kan spara, ladda och återställa Pokemon-data
  genom huvudmenyn.
- Pokemon-data sparas även när programmet avslutas.

### Stridsstatistik

- `battle-stats.json` innehåller statistik över spelarens strider.
- Filen lagrar vinster, förluster, statistik per Pokemon,
  Pokemon-användning och attackanvändning.
- Pokemon identifieras med ID.
- Statistiken sparas automatiskt efter varje avslutad strid.
- Om statistikfilen saknas startar programmet med tom statistik.

### Felhantering

Programmet använder egna undantagsklasser och validerar
användarens inmatning för att undvika krascher.

Om en JSON-fil saknas eller inte kan läsas hanteras felet,
så att programmet kan fortsätta med startdata eller
tom statistik.