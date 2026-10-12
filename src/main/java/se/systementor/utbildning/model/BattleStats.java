package se.systementor.utbildning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BattleStats {

    private int wins;
    private int losses;

    // Används när ny statistik skapas.
    public BattleStats() {
        this(0, 0);
    }

    // Används när statistik läses från JSON.
    @JsonCreator
    public BattleStats(
            @JsonProperty("wins") int wins,
            @JsonProperty("losses") int losses
    ) {
        if (wins < 0 || losses < 0) {
            throw new IllegalArgumentException(
                    "Statistik får inte vara negativ."
            );
        }

        this.wins = wins;
        this.losses = losses;
    }

    // Lägger till en vinst.
    public void addWin() {
        wins++;
    }

    // Lägger till en förlust.
    public void addLoss() {
        losses++;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    @JsonIgnore
    public int getTotalBattles() {
        return wins + losses;
    }
}