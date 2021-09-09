package tr.com.beb.boardgame.domain.model.game;

import tr.com.beb.boardgame.domain.model.Player;

public class Winner {

    private Player player;
    private int points;

    public Winner() {
    }

    public Winner(Player player, int points) {
        this.player = player;
        this.points = points;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}
