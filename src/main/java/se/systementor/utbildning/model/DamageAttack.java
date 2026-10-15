package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

// Representerar en attack som kan orsaka skada på en Pokemon.
// Ärver gemensamma egenskaper och validering från Attack.
// Ansvarar själv för hur skadan beräknas och utförs.
public class DamageAttack extends Attack {

    // Används för slumpmässiga beräkningar under en attack.
    // static gör att samma Random-objekt delas mellan alla DamageAttack.
    private static final Random RANDOM = new Random();

    // Skapar en skadeattack med namn, skada, träffsäkerhet och typ.
    // Jackson använder konstruktorn för att läsa in attacker från JSON.
    @JsonCreator
    public DamageAttack(
            @JsonProperty("name") String name,
            @JsonProperty("baseDamage") int baseDamage,
            @JsonProperty("accuracy") int accuracy,
            @JsonProperty("type") Type type
    ) {
        // Anropar basklassens konstruktor för att
        // validera och spara attackens gemensamma egenskaper.
        super(
                name,
                baseDamage,
                accuracy,
                type
        );
    }

    // Utför en skadeattack mot motståndarens Pokemon.
    // Beräknar träffsäkerhet, typfördel, skada och kritisk träff.
    // Returnerar ett AttackResult med information om utfallet.
    public AttackResult performAttack(Pokemon attacker, Pokemon defender) {

        // Slumpar ett tal mellan 1 och 100 för att avgöra
        // om attacken träffar utifrån dess träffsäkerhet.
        int roll = RANDOM.nextInt(100) + 1;

        // Om det slumpade talet är högre än träffsäkerheten missar attacken.
        // Ingen skada delas ut och resultatet visar att attacken missade.
        if (roll > getAccuracy()) {
            return new AttackResult(false, 0, false, 1.0);
        }

        // Beräknar hur effektiv attackens typ är mot försvararens typ.
        // TypeEffectiveness ansvarar för reglerna om typfördelar.
        double effectiveness = TypeEffectiveness.calculate(
                getType(),
                defender.getType()
        );

        // Slumpar en faktor mellan 0.85 och 1.0.
        // Detta gör att samma attack kan orsaka olika mycket skada.
        double randomFactor = 0.85 + RANDOM.nextDouble() * 0.15;

        // Beräknar skadan utifrån grundskada, typfördel och slumpfaktor.
        // Math.max ser till att en träff alltid gör minst 1 i skada,
        // även om beräkningen avrundas nedåt till 0.
        int damage = Math.max(1, (int) (
                getBaseDamage() * effectiveness * randomFactor
        ));

        // Avgör om attacken blir kritisk.
        // nextInt(16) ger 16 möjliga värden, vilket ger 1/16 chans.
        boolean criticalHit = RANDOM.nextInt(16) == 0;

        // En kritisk träff fördubblar den beräknade skadan.
        if (criticalHit) {
            damage *= 2;
        }

        // Låter försvararens Pokemon själv hantera minskningen av HP.
        // Det följer inkapsling eftersom HP inte ändras direkt här.
        defender.takeDamage(damage);

        // Returnerar resultatet så att stridssystemet kan visa
        // om attacken träffade, skadan, kritisk träff och typfördel.
        return new AttackResult(
                true,
                damage,
                criticalHit,
                effectiveness
        );
    }

    // Implementerar den abstrakta execute-metoden från Attack.
    // BattleService kan därmed utföra handlingen genom BattleAction
    // utan att behöva känna till klassen DamageAttack.
    @Override
    public AttackResult execute(Pokemon attacker, Pokemon defender) {

        // Återanvänder skadeberäkningen och returnerar attackresultatet.
        return performAttack(attacker, defender);
    }
}
