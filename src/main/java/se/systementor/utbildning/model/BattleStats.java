package se.systementor.utbildning.model;

public class BattleStats {

    private int wins;
    private int losses;

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

    public int getTotalBattles() {
        return wins + losses;
    }
}