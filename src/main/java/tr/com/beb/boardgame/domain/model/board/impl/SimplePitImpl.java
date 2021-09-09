package tr.com.beb.boardgame.domain.model.board.impl;

import tr.com.beb.boardgame.domain.model.Player;
import tr.com.beb.boardgame.domain.model.board.Pit;
import tr.com.beb.boardgame.domain.model.board.SimplePit;

public class SimplePitImpl extends Pit implements SimplePit {

    public SimplePitImpl(Player player, int itemCount) {
        super(player, itemCount);
    }

    @Override
    public int collectItems() {
        int result = getItemCount();
        setItemCount(0);
        return result;
    }

    @Override
    public boolean addItem(Player player) {
        setItemCount(getItemCount() + 1);
        return true;
    }

}
