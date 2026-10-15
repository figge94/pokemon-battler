# Pokemon Battler

Pokemon Battler är en konsolbaserad Java-app där användaren kan hantera sin egen Pokedex, strida mot en datorstyrd motståndare och se stridsstatistik.

## Kom igång

Projektet använder Java 21 och Maven.

1. Klona projektet:
```
git clone https://github.com/figge94/pokemon-battler.git
```
2. Öppna projektet i IntelliJ IDEA.
3. Låt Maven läsa in projektets beroenden.
4. Kör klassen `Main`.

Om ingen sparad Pokemon-fil finns laddas startdata från `pokemon-seed.json`.

## Funktioner

### Pokedex

Användaren kan visa, söka, lägga till, redigera och ta bort Pokémon via huvudmenyn.

Varje Pokémon har ett unikt ID, namn, typ, HP, snabbhet och attacker. 
Det går att ändra en Pokemons egenskaper samt lägga till och ta bort attacker.

Pokémon-data kan sparas och läsas från pokemon.json. 
Användaren kan även återställa samlingen till ursprungliga startdata 
från `pokemon-seed.json`.

## Stridssystem

I huvudmenyn kan man välja att spela. För att spela så funkar det såhär:

Spelaren väljer den Pokemon man vill spela med och möter en slumpmässigt
vald motståndare. Striderna är turbaserade och en Pokemons snabbhet avgör
vem som attackerar först. Om båda är lika snabba är det slumpen som avgör.

Spelaren får välja sin handling via en meny medan CPU väljer attacker
genom en strategi. Attacker kan missa, göra varierande skada och orsaka
kritiska träffar.

Striden avslutas när en Pokemon besegras eller spelaren flyr. Efteråt återställs Pokemons HP.

## Skadeberäkning

Skadan beräknas enligt:

**Skada = heltal(grundskada × typeffektivitet × slumpfaktor)**

Vid en kritisk träff multipliceras resultatet med 2.
- **Basskada:** Attackens grundskada.
- **Typeffektivitet:** 2,0 vid typfördel, 0,5 vid typnackdel och annars 1,0 för de typkombinationer som programmet hanterar.
- **Slumpfaktor:** Mellan 0,85 och 1,00, vilket ger variation mellan attacker.
- **Kritisk faktor:** En kritisk träff har 1/16 chans att inträffa och fördubblar skadan. Annars är faktorn 1.

## Stridsstatistik

Programmet sparar statistik över spelarens strider.

Under **Spela** kan användaren välja mellan:

- **Starta strid:** Startar en strid mot en datorstyrd spelare.
- **Total statistik:** Visar totalt antal strider, vinster och förluster samt hur många gånger man valt att fly.
- **Statistik per Pokémon:** Visar antal vinster, förluster, vinstprocent och win/loss-ratio för varje Pokémon som deltagit i strider.
- **Mest använda Pokémon:** Visar vilken Pokemon spelaren har använt flest gånger och antal strider.
- **Mest använda attack:** Visar en lista över spelarens attacker, sorterade efter hur många gånger de använts.

Vinstprocenten beräknas enligt:

**Vinster / (Vinster + Förluster) × 100**

Win/loss-ratio beräknas enligt:

**Vinster / Förluster**

Om en Pokémon har vinster men inga förluster visas ratio som oändlig (`∞`).

Efter avslutad strid sparas statistiken automatiskt i `battle-stats.json` med hjälp av Jackson.

Statistiken kopplas till en Pokemons ID istället för namn, så att tidigare resultat bevaras om en Pokemon byter namn.

Om statistikfilen saknas skapas ny statistik automatiskt.

## Ansvarsfördelning

| Paket           | Ansvar                                                       |
|-----------------|--------------------------------------------------------------|
| `model`         | Pokemon, attacker, strider och statistik                     |
| `service`       | Programmets logik och stridsflöde                            |
| `repository`    | Läser och sparar JSON-filer                                  |
| `ui`            | Menyer, presentation och användarinmatning                   |
| `exception`     | Egna undantagsklasser                                        |
| `strategy`      | CPU-strategier                                               |


Uppdelningen gör det lättare att vidareutveckla olika delar av programmet utan att behöva ändra all kod samtidigt.

## Objektorienterad programmering (OOP)
Projektet använder objektorienterad programmering för att skapa en tydlig struktur och fördela ansvar mellan klasser.

### Inkapsling
I klassen `Pokemon` är attributen privata och nås genom metoder. Klassen kontrollerar själv att HP och antalet attacker följer reglerna. Andra klasser behöver därför inte hantera dessa kontroller.

Även `Battle` kapslar in stridens tillstånd genom metoder som `isOver()`, `playerWon()` och `flee()`.

### Arv och abstraktion
`Attack` är en abstrakt klass som innehåller gemensamma
egenskaper för attacker, såsom namn, typ, grundskada och träffsäkerhet.

`DamageAttack` ärver från `Attack` och implementerar
hur en attack orsakar skada.

På så sätt återanvänds gemensam kod samtidigt som specifikt beteende kan definieras i underklasser.

### Interfaces och polymorfism

`BattleAction` är ett interface som definierar gemensamma metoder för stridshandlingar, exempelvis `execute()` och `label()`.

Både attacker och flykthandlingar kan hanteras genom samma interface. När `execute()` anropas används den implementation som tillhör det aktuella objektet.

På så sätt behöver BattleService inte känna till hur varje handling utförs. Det är ett exempel på polymorfism som gör det lättare att utöka stridssystemet med nya handlingar.

### Enum
`Type` är en enum som innehåller Pokemons olika typer.
Det förhindrar att ogiltiga typnamn används i programmet.

### Strategy Pattern

CPU:ns attackval använder designmönstret Strategy Pattern.

`CpuStrategy` är ett interface med två implementationer.

- `RandomStrategy` väljer en slumpmässig handling. 
- `AggressiveStrategy` prioriterar handlingar som orsakar skada.

CpuPlayer får ett objekt som implementerar `CpuStrategy` genom konstruktorn. 
Därför kan programmet byta mellan `RandomStrategy` och `AggressiveStrategy`
utan att ändra CpuPlayer.
 
Detta visar polymorfism och Dependency Injection.

## Filhantering och felhantering

Programmet använder följande JSON-filer:

- `pokemon-seed.json` – ursprunglig Pokémon-data.

- `pokemon.json` – sparad Pokémon-data.

- `battle-stats.json` – sparad stridsstatistik.

Filhanteringen använder Jackson. Om den sparade Pokemons-filen är ogiltig försöker 
programmet ladda startdata i stället. Saknad eller oläsbar statistikfil 
hanteras genom att skapa tom statistik.

Programmet validerar även användarinmatning och använder egna undantagsklasser
för att hantera fel utan onödiga krascher.

## VG-krav

Projektet innehåller följande funktioner för VG:

- **Typeffektivitet:** `TypeEffectiveness` hanterar samtliga typkombinationer som anges i uppgiften.

- **JSON-persistens:** Pokemon och stridsstatistik sparas och läses med Jackson.

- **Kritiska träffar:** Varje träff har 1/16 chans att bli kritisk och göra dubbel skada. Kritiska träffar visas i stridsloggen.

- **Persistent statistik:** Vinster, förluster, vinst/förlust-ratio per Pokémon samt mest använda Pokemon och attacker sparas mellan programstarter och visas via menyn.

## Kända begränsningar

- Typeffektiviteten omfattar inte alla typkombinationer från de officiella Pokemon-spelen.

- Om sparad Pokemon-data är ogiltig laddas seed-data i stället, om den finns.

- En Pokémon måste ha minst en attack för att vara giltig, vilket gör att strid med en Pokemon utan attacker normalt förhindras redan vid inläsningen.