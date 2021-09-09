package tr.com.beb.boardgame.domain.model.board;

import tr.com.beb.boardgame.domain.model.Player;

public abstract class Pit {

    private Player player;

    private int itemCount;

    public Pit(Player player, int itemCount) {
        this.player = player;
        this.itemCount = itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract boolean addItem(Player player);

}
