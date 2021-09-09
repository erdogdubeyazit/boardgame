package tr.com.beb.boardgame.domain.model.board.impl;

import tr.com.beb.boardgame.domain.model.Player;
import tr.com.beb.boardgame.domain.model.board.Pit;
import tr.com.beb.boardgame.domain.model.board.Tank;

public class TankImpl extends Pit implements Tank {

    public TankImpl(Player player, int itemCount) {
        super(player, itemCount);
    }

    @Override
    public void addItems(int itemCount) {
        setItemCount(getItemCount() + itemCount);

    }

    @Override
    public boolean addItem(Player player) {
        if (player == getPlayer()) {
            setItemCount(getItemCount() + 1);
            return true;
        } else
            return false;
    }

}
